package com.github.black0nion.blackonionbot.systems.voicerecognition;

import java.util.HashMap;
import java.util.logging.Logger;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SpeechRecording extends ListenerAdapter {
	
	public static StreamSpeechRecognizer recognizer;
	
	private static HashMap<Long, UserVoiceThingy> thingys = new HashMap<>();
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		if (!event.getMessage().getContentRaw().equalsIgnoreCase("joinforvoice"))
			return;
		
		VoiceChannel userVoiceChannel = event.getMember().getVoiceState().getChannel();
		
		Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resources/model.dic");
        configuration.setLanguageModelPath("resources/model.lm");
        
      //prevent logging spam of sphinx4
        Logger cmRootLogger = Logger.getLogger("default.config");
        cmRootLogger.setLevel(java.util.logging.Level.OFF);
        String conFile = System.getProperty("java.util.logging.config.file");
        if (conFile == null) {
            System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
        }
        
        try {
        	userVoiceChannel.getGuild().getAudioManager().openAudioConnection(userVoiceChannel);
        } catch (Exception e) {
        	System.out.println("Error while Joining " + e.getMessage());
        }
        
        try {
	        recognizer = new StreamSpeechRecognizer(configuration);
	        
	        userVoiceChannel.getGuild().getAudioManager().setReceivingHandler(new AudioReceiveHandler() {
	            @Override
	            public boolean canReceiveCombined() {
	                return false;
	            }

	            @Override
	            public boolean canReceiveUser() {
	                return true;
	            }

	            @Override
	            public void handleCombinedAudio(CombinedAudio combinedAudio) {
	               
	            }

	            @Override
	            public void handleUserAudio(UserAudio userAudio) {
	            	if (thingys.containsKey(userAudio.getUser().getIdLong())) {
	            		final UserVoiceThingy userVoiceThingy = thingys.get(userAudio.getUser().getIdLong());
	            		userVoiceThingy.addToPacket(userAudio.getAudioData(1));
	            	} else {
	            		final UserVoiceThingy userVoiceThingy = new UserVoiceThingy(userAudio.getUser(), userAudio.getAudioData(1));
	            		thingys.put(userAudio.getUser().getIdLong(), userVoiceThingy);
	            	}
	            }
	        });
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
}
