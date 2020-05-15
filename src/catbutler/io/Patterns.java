package catbutler.io;

public class Patterns {

    public static String crsFoodSetPattern = "(?i)f(ood)?:\\s*.*";
    public static String crsReactionPattern = "(.*):(.*)(\\[.*\\])?(\\{.*\\})?(<?[-=]>)(.*)";
    public static String dnfPattern = "((^\\s*|\\s*&\\s*|\\s*,\\s*)([\\d]+(\\.[\\d]+)?)?\\s*([A-Za-z_])([A-Za-z0-9_'-])*)+";

}
