package org.devtimize.urm;

//TODO
public class DomainMapperCli {

    /*private static final Logger log = LoggerFactory.getLogger(DomainMapperCli.class);
    DomainMapper domainMapper;

    public static void main(final String[] args) throws ClassNotFoundException, IOException {
        new DomainMapperCli().run(args);
    }

    public void run(final String[] args) throws ClassNotFoundException, IOException {
        // create the command line parser
        CommandLineParser parser = new BasicParser();
        // create the Options
        Options options = new Options();
        options.addOption("f", "file", true, "write to file");
        options.addOption("p", "package", true, "comma separated list of domain packages");
        options.addOption(OptionBuilder.withArgName("package").hasArgs().isRequired().create('p'));
        options.addOption("i", "ignore", true, "comma separated list of ignored types");
        options.addOption(OptionBuilder.withArgName("ignore").hasArgs().isRequired(false).create('i'));
        options.addOption("s", "presenter", true, "presenter to be used");
        options.addOption(OptionBuilder.withArgName("presenter").hasArgs().isRequired(false).create('s'));
        try {
            CommandLine line = parser.parse(options, args);
            String[] packages = line.getOptionValue("p").split(",[ ]*");
            log.debug("Scanning domain for packages:");
            for (String packageName : packages) {
                log.debug(packageName);
            }
            String[] ignores = null;
            if (line.hasOption("i")) {
                ignores = line.getOptionValue("i").split(",[ ]*");
                if (ignores != null) {
                    log.debug("Ignored types:");
                    for (String ignore : ignores) {
                        log.debug(ignore);
                    }
                }
            }

            Presenter presenter = Presenter.parse(line.getOptionValue("s"));
            domainMapper = DomainMapper.create(presenter, Arrays.asList(packages), ignores == null ? new ArrayList<>() : Arrays.asList(ignores));
            Representation representation = domainMapper.describeDomain();
            if (line.hasOption('f')) {
                String filename = line.getOptionValue('f');
                Files.write(Paths.get(filename), representation.getContent().getBytes());
                log.info("Wrote to file " + filename);
            } else {
                log.info(representation.getContent());
            }
        } catch (ParseException exp) {
            log.info(exp.getMessage());
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar urm-core.jar", options);
        }
    }*/
}
