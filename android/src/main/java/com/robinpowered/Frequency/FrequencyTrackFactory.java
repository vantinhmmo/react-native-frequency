package com.robinpowered.Frequency;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioAttributes;
import android.os.Build;

class FrequencyTrackFactory {
    // number of samples of audio carried per second - higher for better quality/clarity
    static final int SAMPLE_RATE = 5000;

    public static AudioTrack create(double freqHz, double duration) {
        int durationMs = (int) duration;
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
        AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
        count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }


    /**
     * Generates an AudioTrack instance that plays a specific frequency
     * for a certain duration
     *
     * @param frequency Frequency of the audio track
     * @param duration Duration of the audio track
     * @return AudioTrack instance
     */
    public static AudioTrack createsd(double frequency, double Nduration) {
        int duration = (int) Nduration; // Duration of the tone in seconds
        int sampleRate = 5000; // Sample rate of the tone

        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int maxBufferSize = 4096; // Set your desired maximum buffer size

        // Adjust the buffer size if it exceeds the maximum buffer size
        if (bufferSize > maxBufferSize) {
            bufferSize = maxBufferSize;
        }

        int numSamples = duration * sampleRate;
        short[] samples = new short[numSamples];

        // Generate the samples for the tone
        for (int i = 0; i < numSamples; i++) {
            double time = i / (double) sampleRate;
            samples[i] = (short) (Math.sin(2 * Math.PI * frequency * time) * Short.MAX_VALUE);
        }

        // Create an AudioTrack with the desired buffer size
        AudioTrack audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(bufferSize * 2) // 2 bytes per sample for ENCODING_PCM_16BIT
                .build();

        audioTrack.play();
        // Write the samples to the AudioTrack
        audioTrack.write(samples, 0, numSamples);

        // callback when track finishes playing
        audioTrack.setNotificationMarkerPosition(numSamples / 2);

        return audioTrack;
    }

    static byte[] createSoundData(double frequency, int numberOfSamples) {
        double sample[] = new double[numberOfSamples];
        byte soundData[]= new byte[2 * numberOfSamples];

        for (int i = 0; i < numberOfSamples; i++) {
            sample[i] = Math.sin(2 * Math.PI * i / (SAMPLE_RATE/frequency));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        for (double dVal : sample) {
            short val = (short) (dVal * 32767);
            soundData[idx++] = (byte) (val & 0x00ff);
            soundData[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        return soundData;
    }
}
