package com.github.black0nion.blackonionbot.systems.voicerecognition;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.User;

public class UserVoiceThingy {
	private byte[] packet;
	private User user;
	
	public UserVoiceThingy(User user, byte[] packet) {
		this.user = user;
		this.packet = packet;
	}
	
	public void addToPacket(byte[] toAdd) {
		int length = (packet != null ? packet.length : toAdd.length);
	    if (length >= 576000) {
	        try {
	        	getResult(SpeechRecording.recognizer);
	        } catch (Exception e) { e.printStackTrace(); }
	        packet = toAdd; // reset the packet
	        return;
	    }

	    byte[] newPacket = new byte[length + 3840];
	    // copy old packet onto new temp array
	    System.arraycopy(packet, 0, newPacket, 0, length);
	    // copy toAdd packet onto new temp array
	    System.arraycopy(toAdd, 0, newPacket, 3840, toAdd.length);
	    // overwrite the old packet with the newly resized packet
	    packet = newPacket;
	}
	
	public void getResult(StreamSpeechRecognizer recognizer) throws FileNotFoundException {
		 // Specify the output format you want
        AudioFormat target = new AudioFormat(16000f, 16, 1, true, false);
        // Get the audio stream ready, and pass in the raw byte[]
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet);
		final AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, AudioReceiveHandler.OUTPUT_FORMAT, packet.length);
		AudioInputStream is = AudioSystem.getAudioInputStream(target, audioInputStream);
        // Write a temporary file to the computer somewhere, this method will return a InputStream that can be used for recognition
        try {
            AudioSystem.write(is, AudioFileFormat.Type.WAVE, new File(user.getId() + ".wav"));
        } catch(Exception e) {e.printStackTrace();}
        
	    InputStream stream = new FileInputStream(user.getId() + ".wav");
	    recognizer.startRecognition(stream);
	    SpeechResult result;
	    while ((result = recognizer.getResult()) != null) {
	        System.out.format("Hypothesis: %s\n", result.getHypothesis());
	    }
	    recognizer.stopRecognition();
	}
	
}
