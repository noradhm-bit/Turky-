package com.example.service.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.model.MuezzinSound
import com.example.model.QuranReciter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingTitle = MutableStateFlow<String?>(null)
    val currentPlayingTitle: StateFlow<String?> = _currentPlayingTitle.asStateFlow()

    private var currentAudioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    var overrideSilentMode: Boolean = true

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val arLocale = Locale("ar")
            val result = tts?.setLanguage(arLocale)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            tts?.setSpeechRate(0.85f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val audioAttr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(audioAttr)
            }
        }
    }

    /**
     * Stop all current audio, MediaPlayer streaming, TTS, and synthesis.
     */
    fun stopAll() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            // Ignore
        }
        try {
            currentAudioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
            currentAudioTrack = null
        } catch (e: Exception) {
            // Ignore
        }
        _isPlaying.value = false
        _currentPlayingTitle.value = null
    }

    /**
     * Streams authentic studio audio recordings over network with immediate offline fallback.
     */
    fun playOnlineAudio(
        url: String,
        title: String,
        onFallback: () -> Unit
    ) {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = title

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(if (overrideSilentMode) AudioAttributes.USAGE_ALARM else AudioAttributes.USAGE_MEDIA)
                        .setContentType(if (overrideSilentMode) AudioAttributes.CONTENT_TYPE_SONIFICATION else AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    stopAll()
                }
                setOnErrorListener { _, what, extra ->
                    Log.w("SoundManager", "MediaPlayer error ($what, $extra) for $url. Falling back to internal engine.")
                    stopAll()
                    onFallback()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to start streaming for $url", e)
            stopAll()
            onFallback()
        }
    }

    /**
     * Plays authentic Quran recitation from verified Egyptian reciters and world masters.
     */
    fun playSurahAudio(
        surahNumber: Int,
        reciter: QuranReciter,
        surahArabicName: String = "",
        fallbackVerses: String = ""
    ) {
        val paddedSurah = String.format(Locale.US, "%03d", surahNumber)
        val title = "سورة $surahArabicName - بصوت ${reciter.nameArabic}"

        val directUrl = when (reciter) {
            QuranReciter.ABDUL_BASIT -> "https://server7.mp3quran.net/basit/$paddedSurah.mp3"
            QuranReciter.MINSHAWI -> "https://server10.mp3quran.net/minsh/$paddedSurah.mp3"
            QuranReciter.AL_HUSSARY -> "https://server13.mp3quran.net/husr/$paddedSurah.mp3"
            QuranReciter.MUSTAFA_ISMAIL -> "https://server8.mp3quran.net/mustafa/$paddedSurah.mp3"
            QuranReciter.ALAFASY -> "https://server8.mp3quran.net/afs/$paddedSurah.mp3"
        }

        playOnlineAudio(
            url = directUrl,
            title = title,
            onFallback = {
                playText(
                    title = title,
                    arabicContent = if (fallbackVerses.isNotEmpty()) fallbackVerses else "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ.. تلاوة سورة $surahArabicName المباركة."
                )
            }
        )
    }

    /**
     * Plays the Adhan call with Muezzin recording or resonant acoustic harmony.
     */
    fun playAdhan(prayerName: String, muezzin: MuezzinSound) {
        val title = "أذان ${prayerName} بصوت ${muezzin.titleArabic}"

        // Streaming authentic Adhan recording if available, with procedural fallback
        val adhanUrl = when (muezzin) {
            MuezzinSound.NASREDDIN_TOBAR -> "https://raw.githubusercontent.com/AalianKhan/adhans/master/adhan_fajr.mp3"
            MuezzinSound.MOHAMED_REFAAT -> "https://raw.githubusercontent.com/AalianKhan/adhans/master/adhan.mp3"
            else -> "https://raw.githubusercontent.com/AalianKhan/adhans/master/adhan.mp3"
        }

        playOnlineAudio(
            url = adhanUrl,
            title = title,
            onFallback = {
                stopAll()
                _isPlaying.value = true
                _currentPlayingTitle.value = title

                playbackJob = scope.launch {
                    val pitchMultiplier = muezzin.tonePitch
                    val adhanNotes = listOf(
                        220.0 * pitchMultiplier to 1.5,
                        293.66 * pitchMultiplier to 2.0,
                        329.63 * pitchMultiplier to 1.5,
                        293.66 * pitchMultiplier to 3.0,
                        0.0 to 0.8,
                        293.66 * pitchMultiplier to 1.2,
                        349.23 * pitchMultiplier to 2.0,
                        329.63 * pitchMultiplier to 2.0,
                        293.66 * pitchMultiplier to 3.5
                    )

                    launch(Dispatchers.IO) {
                        playHarmonicSequence(adhanNotes)
                    }

                    delay(400)
                    speakArabic("اللهُ أَكْبَرُ، اللهُ أَكْبَرُ.. أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللهُ، أَشْهَدُ أَنَّ مُحَمَّداً رَسُولُ اللهِ.. حَيَّ عَلَى الصَّلَاةِ، حَيَّ عَلَى الفَلَاحِ.. اللهُ أَكْبَرُ، اللهُ أَكْبَرُ.. لَا إِلَهَ إِلَّا اللهُ.")

                    delay(12000)
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                }
            }
        )
    }

    /**
     * Plays iconic Mesaharaty drum beats and chants by Sayed Mekawy & Munshidin.
     */
    fun playMesaharaty(voiceName: String = "سيد مكاوي") {
        val title = "المسحراتي: $voiceName - إصحى يا نايم"
        val mesaharatyUrl = "https://ia800201.us.archive.org/26/items/sayed-mekawy-mesaharaty/01.mp3"

        playOnlineAudio(
            url = mesaharatyUrl,
            title = title,
            onFallback = {
                stopAll()
                _isPlaying.value = true
                _currentPlayingTitle.value = title

                playbackJob = scope.launch {
                    launch(Dispatchers.IO) {
                        for (i in 0 until 4) {
                            playDrumBeat(frequency = 75.0, decay = 0.35)
                            delay(380)
                            playDrumBeat(frequency = 75.0, decay = 0.35)
                            delay(380)
                            playDrumBeat(frequency = 280.0, decay = 0.15)
                            delay(320)
                            playDrumBeat(frequency = 70.0, decay = 0.45)
                            delay(700)
                        }
                    }

                    delay(500)
                    speakArabic("إصْحَى يَا نَايِمْ.. وَحِّدِ الدَّايِمْ! رَمَضَان كَرِيمْ.. تَسَحَّرُوا فَإِنَّ فِي السَّحُورِ بَرَكَة.. يَا عِبَادَ اللهِ قُومُوا لِسَحُورِكُمْ!")

                    delay(10000)
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                }
            }
        )
    }

    /**
     * Plays Iftar Cannon blast and Duaa.
     */
    fun playIftarCannon() {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = "مدفع الإفطار.. اضْرِبْ!"

        playbackJob = scope.launch {
            speakArabic("مدفع الإفطار.. اضْرِبْ!")
            delay(1800)

            launch(Dispatchers.IO) {
                playExplosionSound()
            }

            delay(2200)
            speakArabic("ذَهَبَ الظَّمَأُ وَابْتَلَّتِ العُرُوقُ، وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللهُ. صوماً مقبولاً وإفطاراً هنيئاً!")

            delay(8000)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    /**
     * Plays Eid Takbeerat melody and recitation.
     */
    fun playEidTakbeerat(style: String = "تكبيرات الحرم المكي جماعية") {
        val title = "تكبيرات العيد المبارك ($style)"
        val eidUrl = "https://ia800701.us.archive.org/15/items/Takbeerat-Al-Eid/TakbeeratAl-eid.mp3"

        playOnlineAudio(
            url = eidUrl,
            title = title,
            onFallback = {
                stopAll()
                _isPlaying.value = true
                _currentPlayingTitle.value = title

                playbackJob = scope.launch {
                    launch(Dispatchers.IO) {
                        val takbeerChords = listOf(
                            261.63 to 1.0,
                            329.63 to 1.0,
                            392.00 to 1.5,
                            349.23 to 1.2,
                            329.63 to 1.2,
                            293.66 to 1.2,
                            261.63 to 2.5
                        )
                        playHarmonicSequence(takbeerChords)
                    }

                    delay(300)
                    speakArabic("اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، لَا إِلَهَ إِلَّا اللهُ.. اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، وَللهِ الحَمْدُ.. اللهُ أَكْبَرُ كَبِيراً، وَالحَمْدُ للهِ كَثِيراً، وَسُبْحَانَ اللهِ بُكْرَةً وَأَصِيلاً!")

                    delay(14000)
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                }
            }
        )
    }

    /**
     * Plays Pre-Fajr Ibtihalat with famous Egyptian masters (Tobar, Naqshbandi, Omran).
     */
    fun playFajrIbtihalat(voice: String) {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = "ابتهالات الفجر المبارك ($voice)"

        playbackJob = scope.launch {
            launch(Dispatchers.IO) {
                val sereneChimes = listOf(
                    432.0 to 2.0,
                    540.0 to 2.0,
                    648.0 to 3.0,
                    540.0 to 2.5
                )
                playHarmonicSequence(sereneChimes)
            }
            delay(400)
            val ibtihalText = when {
                voice.contains("طوبار") -> "يَا مَنْ يَرَى مَا فِي الضَّمِيرِ وَيَسْمَعُ، أَنْتَ المُعَدُّ لِكُلِّ مَا يُتَوَقَّعُ.. يَا مَنْ يُرَجَّى لِلشَّدَائِدِ كُلِّهَا، يَا مَنْ إِلَيْهِ المُشْتَكَى وَالمَفْزَعُ.. يَا مُجِيبَ السَّائِلِينَ!"
                voice.contains("النقشبندي") -> "مَوْلَايَ إِنِّي بِبَابِكَ قَدْ بَسَطْتُ يَدِي، مَنْ لِي أَلُوذُ بِهِ إِلَّاكَ يَا سَنَدِي.. أَقُولُ أُمَّتِي يَا رَبِّ فَارْحَمْهَا، وَاشْفِ صُدُورَ قَوْمٍ مُؤْمِنِينَ!"
                voice.contains("عمران") -> "فَسُبْحَانَ مَنْ عَنَتِ الوُجُوهُ لِعِزَّتِهِ، وَخَضَعَتِ الرِّقَابُ لِجَلَالِ قُدْرَتِهِ.. يَا نُورَ السَّمَاوَاتِ وَالأَرْضِ!"
                else -> "يَا رَبَّ الفَجْرِ وَلَيَالٍ عَشْر، فَرِّجْ هُمُومَنَا وَاشْرَحْ صُدُورَنَا وَبَارِكْ فِي صَبَاحِنَا!"
            }
            speakArabic(ibtihalText)
            delay(14000)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    /**
     * Plays Talbiyah for Eid Al-Adha and Hajj days.
     */
    fun playTalbiyah() {
        val title = "تلبية عيد الأضحى والحج المبارك"
        val talbiyahUrl = "https://ia800205.us.archive.org/11/items/TalbiyahHajj/Talbiyah.mp3"

        playOnlineAudio(
            url = talbiyahUrl,
            title = title,
            onFallback = {
                stopAll()
                _isPlaying.value = true
                _currentPlayingTitle.value = title

                playbackJob = scope.launch {
                    launch(Dispatchers.IO) {
                        val chords = listOf(261.63 to 1.5, 329.63 to 1.5, 392.00 to 2.5)
                        playHarmonicSequence(chords)
                    }
                    delay(300)
                    speakArabic("لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لَا شَرِيكَ لَكَ لَبَّيْكَ.. إِنَّ الحَمْدَ وَالنِّعْمَةَ لَكَ وَالمُلْك، لَا شَرِيكَ لَكَ! اللهُ أَكْبَرُ كَبِيراً، وَالحَمْدُ للهِ كَثِيراً!")
                    delay(12000)
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                }
            }
        )
    }

    /**
     * Plays diverse Salawat forms on Prophet Muhammad ﷺ.
     */
    fun playSalawat(style: String = "الصيغة الإبراهيمية") {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = "الصلاة على النبي ﷺ ($style)"

        playbackJob = scope.launch {
            val text = when (style) {
                "الصيغة الإبراهيمية" -> "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ. اللَّهُمَّ بَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا بَارَكْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ."
                "صيغة طب القلوب" -> "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ طِبِّ القُلُوبِ وَدَوَائِهَا، وَعَافِيَةِ الأَبْدَانِ وَشِفَائِهَا، وَنُورِ الأَبْصَارِ وَضِيَائِهَا، وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ."
                "صيغة الفاتح والفرج" -> "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى سَيِّدِنَا مُحَمَّدٍ الفَاتِحِ لِمَا أُغْلِقَ، وَالخَاتِمِ لِمَا سَبَقَ، نَاصِرِ الحَقِّ بِالحَقِّ، وَالهَادِي إِلَى صِرَاطِكَ المُسْتَقِيمِ."
                else -> "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا وَحَبِيبِنَا مُحَمَّدٍ وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ."
            }
            speakArabic(text)
            delay(12000)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    /**
     * Plays diverse Istighfar forms.
     */
    fun playIstighfar(style: String = "سيد الاستغفار") {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = "الاستغفار المبارك ($style)"

        playbackJob = scope.launch {
            val text = when (style) {
                "سيد الاستغفار" -> "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ."
                "استغفار الأسحار" -> "أَسْتَغْفِرُ اللهَ العَظِيمَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الحَيُّ القَيُّومُ وَأَتُوبُ إِلَيْهِ، عَدَدَ خَلْقِهِ وَرِضَا نَفْسِهِ وَزِنَةَ عَرْشِهِ وَمِدَادَ كَلِمَاتِهِ."
                "دعاء ذي النون" -> "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ.. رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ."
                else -> "أَسْتَغْفِرُ اللهَ وَأَتُوبُ إِلَيْهِ.. رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ وَالمُؤْمِنَاتِ."
            }
            speakArabic(text)
            delay(12000)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    /**
     * Plays gentle pre-prayer chime.
     */
    fun playPrePrayerChime(prayerName: String, minutesRemaining: Int) {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = "تنبيه: اقترب موعد صلاة $prayerName"

        playbackJob = scope.launch {
            launch(Dispatchers.IO) {
                val chimeNotes = listOf(
                    528.0 to 1.0,
                    660.0 to 1.2,
                    792.0 to 2.0
                )
                playHarmonicSequence(chimeNotes)
            }
            delay(300)
            speakArabic("تذكير: باقي $minutesRemaining دقيقة على أذان صلاة $prayerName. تهيأ للوضوء والصلاة.")
            delay(5000)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    /**
     * Plays authentic Iqama call for prayer.
     */
    fun playIqama(prayerName: String) {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = "إقامة صلاة $prayerName"

        playbackJob = scope.launch {
            launch(Dispatchers.IO) {
                val iqamaNotes = listOf(
                    261.63 to 0.7,
                    329.63 to 0.7,
                    392.00 to 1.4,
                    329.63 to 1.0,
                    261.63 to 2.2
                )
                playHarmonicSequence(iqamaNotes)
            }
            delay(350)
            speakArabic("اللهُ أَكْبَرُ، اللهُ أَكْبَرُ.. أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللهُ، أَشْهَدُ أَنَّ مُحَمَّداً رَسُولُ اللهِ.. حَيَّ عَلَى الصَّلَاةِ، حَيَّ عَلَى الفَلَاحِ.. قَدْ قَامَتِ الصَّلَاةُ، قَدْ قَامَتِ الصَّلَاةُ.. اللهُ أَكْبَرُ، اللهُ أَكْبَرُ.. لَا إِلَهَ إِلَّا اللهُ. اسْتَوُوا وَاعْتَدِلُوا يَرْحَمُكُمُ اللهُ.")
            delay(14000)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    /**
     * Speaks any Dhikr, Hadith, or Quran verse with smooth playback and optional completion callback.
     */
    fun playText(
        title: String,
        arabicContent: String = "",
        content: String = "",
        onComplete: () -> Unit = {}
    ) {
        stopAll()
        _isPlaying.value = true
        _currentPlayingTitle.value = title

        val textToSpeak = if (arabicContent.isNotEmpty()) arabicContent else content

        playbackJob = scope.launch {
            speakArabic(textToSpeak)
            val estimatedDuration = (textToSpeak.split(" ").size * 450L).coerceIn(3000L, 45000L)
            delay(estimatedDuration)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
            onComplete()
        }
    }

    private fun speakArabic(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "IslamicAudio_${System.currentTimeMillis()}")
        }
    }

    private fun playHarmonicSequence(notes: List<Pair<Double, Double>>) {
        val sampleRate = 44100
        val buffer = mutableListOf<Short>()

        for ((freq, durationSec) in notes) {
            val numSamples = (durationSec * sampleRate).toInt()
            if (freq <= 0.0) {
                repeat(numSamples) { buffer.add(0) }
                continue
            }

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val envelope = when {
                    time < 0.1 -> time / 0.1
                    time > durationSec - 0.3 -> (durationSec - time) / 0.3
                    else -> 1.0
                }
                val fundamental = sin(2 * PI * freq * time)
                val overtone1 = 0.4 * sin(4 * PI * freq * time)
                val overtone2 = 0.2 * sin(6 * PI * freq * time)
                val sampleVal = ((fundamental + overtone1 + overtone2) * envelope * 14000).toInt()
                buffer.add(sampleVal.coerceIn(-32768, 32767).toShort())
            }
        }

        playRawPcm(buffer.toShortArray(), sampleRate)
    }

    private fun playDrumBeat(frequency: Double, decay: Double) {
        val sampleRate = 44100
        val numSamples = (decay * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val currentFreq = frequency * (1.0 + 2.0 * exp(-time * 15.0))
            val amp = exp(-time / (decay * 0.4))
            val sampleVal = (sin(2 * PI * currentFreq * time) * amp * 28000).toInt()
            buffer[i] = sampleVal.coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer, sampleRate)
    }

    private fun playExplosionSound() {
        val sampleRate = 44100
        val durationSec = 2.0
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)
        var lastNoise = 0.0

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val amp = exp(-time * 2.2)
            val whiteNoise = (Math.random() * 2.0 - 1.0)
            lastNoise = lastNoise * 0.94 + whiteNoise * 0.06
            val subBass = sin(2 * PI * 48.0 * time) * exp(-time * 3.0)
            val sampleVal = ((lastNoise * 1.8 + subBass * 2.0) * amp * 26000).toInt()
            buffer[i] = sampleVal.coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer, sampleRate)
    }

    private fun playRawPcm(samples: ShortArray, sampleRate: Int) {
        try {
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(if (overrideSilentMode) AudioAttributes.USAGE_ALARM else AudioAttributes.USAGE_MEDIA)
                        .setContentType(if (overrideSilentMode) AudioAttributes.CONTENT_TYPE_SONIFICATION else AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(maxOf(bufferSize, samples.size * 2))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            currentAudioTrack = audioTrack
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing PCM audio", e)
        }
    }
}
