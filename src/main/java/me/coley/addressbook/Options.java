package me.coley.addressbook;

import picocli.CommandLine;

/**
 * Launch arguments.
 */
public class Options {
	@CommandLine.Option(
			names = "-sport",
			description = "SparkJava port",
			showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
	private int sparkPort = 25565;
	@CommandLine.Option(
			names = "-eport",
			description = "ElasticSearch port",
			showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
	private int elasticPort = 9200;
	@CommandLine.Option(
			names = "-eversion",
			description = "ElasticSearch version",
			showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
	private String elasticVersion = "6.8.6";
	@CommandLine.Option(
			names = "-existing",
			description = "Use an currently running ElasticSearch server",
			showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
	private boolean useExisting;
	@CommandLine.Option(
			names = "-help",
			usageHelp = true, hidden = true)
	private boolean help;

	/**
	 * @return SparkJava port.
	 */
	public int getSparkPort() {
		return sparkPort;
	}

	/**
	 * @param sparkPort
	 * 		SparkJava port.
	 */
	public void setSparkPort(int sparkPort) {
		this.sparkPort = sparkPort;
	}

	/**
	 * @return ElasticSearch port.
	 */
	public int getElasticPort() {
		return elasticPort;
	}

	/**
	 * @param elasticPort
	 * 		ElasticSearch port.
	 */
	public void setElasticPort(int elasticPort) {
		this.elasticPort = elasticPort;
	}

	/**
	 * @return ElasticSearch version.
	 */
	public String getElasticVersion() {
		return elasticVersion;
	}

	/**
	 * @param elasticVersion
	 * 		ElasticSearch version.
	 */
	public void setElasticVersion(String elasticVersion) {
		this.elasticVersion = elasticVersion;
	}

	/**
	 * @return {@code true} if the server should use an currently running ElasticSearch server.
	 * {@code false} otherwise.
	 */
	public boolean useExisting() {
		return useExisting;
	}

	/**
	 * @param useExisting
	 *        {@code true} if the server should use an currently running ElasticSearch server.
	 *        {@code false} otherwise.
	 */
	public void setUseExisting(boolean useExisting) {
		this.useExisting = useExisting;
	}

	/**
	 * @return {@code true} if user requested command info, {@code false} otherwise.
	 */
	public boolean wasHelpRequested() {
		return help;
	}
}
