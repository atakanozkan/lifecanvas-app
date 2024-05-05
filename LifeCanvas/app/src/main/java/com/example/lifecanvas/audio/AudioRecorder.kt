package com.example.lifecanvas.audio

import android.content.Context
import android.media.MediaRecorder
import android.media.MediaPlayer
import java.io.File
import java.io.IOException
import android.util.Log
import com.example.lifecanvas.viewModel.NoteViewModel
import com.example.lifecanvas.model.NoteModel
class AudioRecorder(context: Context, fileName: String) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false
    private val filePath: String = context.getExternalFilesDir(null)?.absolutePath + File.separator + fileName

    private var onPlaybackComplete: (() -> Unit)? = null

    fun setOnPlaybackCompleteListener(listener: () -> Unit) {
        onPlaybackComplete = listener
    }
    fun startRecording() {
        recorder = MediaRecorder().apply {
            try {
                reset()
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setOutputFile(filePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                prepare()
                start()
                isRecording = true
                Log.d("AudioRecorder", "Recording started!")
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Error occurred with media recorder!!!", e)
            }
        }
    }

    fun stopRecording(noteViewModel: NoteViewModel, note: NoteModel) {
        if (isRecording) {
            try {
                recorder?.apply {
                    stop()
                    release()
                }
                Log.d("AudioRecorder", "Recording stopped!")
            } catch (e: IllegalStateException) {
                Log.e("AudioRecorder", "Error stopping media recorder!", e)
            } finally {
                recorder = null
                isRecording = false
                val updatedNote = note.copy(filePath = filePath)
                noteViewModel.update(updatedNote)
            }
        } else {
            Log.d("AudioRecorder", "Recording stop without recording!")
        }
    }

    fun playRecording() {
        player = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                start()
                this@AudioRecorder.isPlaying = true
                setOnCompletionListener {
                    this@AudioRecorder.isPlaying = false
                    onPlaybackComplete?.invoke()
                }
            } catch (e: IOException) {
                Log.e("AudioRecorder", "Error occurred during playing!!!", e)
            }
        }
        Log.d("AudioRecorder", "The audio started playing!")
    }

    fun stopPlaying() {
        if (isPlaying) {
            player?.apply {
                stop()
                release()
            }
            player = null
            isPlaying = false
            Log.d("AudioRecorder", "Stopped playing the audio!")
        }
    }

    fun deleteRecording() {
        File(filePath).delete()
        Log.d("AudioRecorder", "The audio file deleted!")
    }
}
