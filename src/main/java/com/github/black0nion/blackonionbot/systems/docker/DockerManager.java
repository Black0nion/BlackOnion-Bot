package com.github.black0nion.blackonionbot.systems.docker;

import com.github.black0nion.blackonionbot.misc.Reloadable;

public class DockerManager {
	@Reloadable("docker")
	public static void init() {
		// Will get added by the SlashCommands branch
		/*
		 * DockerClientConfig standard =
		 * DefaultDockerClientConfig.createDefaultConfigBuilder().build();
		 * 
		 * DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
		 * .dockerHost(standard.getDockerHost()) .sslConfig(standard.getSSLConfig())
		 * .connectTimeout(2000) .readTimeout(2000) .build();
		 * 
		 * DockerClient dockerClient = DockerClientImpl.getInstance(standard,
		 * httpClient); dockerClient.listContainersCmd().exec() .stream()
		 * .map(Container::getNames) .map(Arrays::toString)
		 * .forEach(System.out::println);
		 */
	}
}
