package jade;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {
  private int bufferID;
  private int sourceID;
  private String filePath;

  private boolean isPlaying = false;

  public Sound(String filePath, boolean loops) {
    this.filePath = filePath;

    // Allocate the memory for the audio information return from stb.
    stackPush();
    IntBuffer channelBuffer = stackMallocInt(1);
    stackPush();
    IntBuffer sampleRateBuffer = stackMallocInt(1);

    ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filePath, channelBuffer, sampleRateBuffer);

    if (rawAudioBuffer == null) {
      System.out.println("Could not decode audio file: " + filePath);
      stackPop();
      stackPop();
      return;
    }

    // Success. Retrieve the extra information that was stored in the buffers by stb
    int channels = channelBuffer.get(0);
    int sampleRate = sampleRateBuffer.get(0);

    // Free
    stackPop();
    stackPop();

    // Find the correct openAL format

    int format = -1;
    if (channels == 1) {
      format = AL_FORMAT_MONO16;
    } else if (channels == 2) {
      format = AL_FORMAT_STEREO16;
    } else {
      System.out.println("Warning:");
      System.out.println("Unknown channels: " + channels);
    }

    bufferID = alGenBuffers();
    alBufferData(bufferID, format, rawAudioBuffer, sampleRate);

    // Generate the source
    sourceID = alGenSources();
    alSourcei(sourceID, AL_BUFFER, bufferID);

    alSourcei(sourceID, AL_LOOPING, loops ? 1 : 0);

    alSourcei(sourceID, AL_POSITION, 0);

    alSourcef(sourceID, AL_GAIN, 0.3f);

    // Free stb raw audio buffer.
    free(rawAudioBuffer);
  }

  public void deleteSound() {
    alDeleteSources(sourceID);
    alDeleteBuffers(bufferID);
  }

  public void playSound() {
    int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
    if (state == AL_STOPPED) {
      isPlaying = false;
      alSourcei(sourceID, AL_POSITION, 0);
    }

    if (!isPlaying) {
      alSourcePlay(sourceID);
      isPlaying = true;
    }
  }

  public void stopSound() {
    if (isPlaying) {
      alSourceStop(sourceID);
      isPlaying = false;
    }
  }

  public String getFilePath() {
    return filePath;
  }

  public boolean isPlaying() {
    int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
    if (state == AL_STOPPED) {
      isPlaying = false;
    }
    return isPlaying;
  }
}
