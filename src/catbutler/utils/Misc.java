package catbutler.utils;

import catbutler.gui.ui.QuickInfoPopup;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Misc {

    public static boolean isValidParsingScheme(File xmlFileIn, boolean tryRepair) {
        File schemaFile = new File("src/catbutler/resources/schemes/fileParsingScheme.xsd");
        Source xmlFile = new StreamSource(xmlFileIn);
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            return true;
        } catch (SAXException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            return false;
        } catch (IOException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        return false;
    }

    public static String correctSBOTerm(String sboTerm, String parent) {
        if (sboTerm.matches("(SBO:)?\\d{7}")) {
            return sboTerm;
        } else {
            switch (parent) {
                case "species":
                    return "SBO:0000241";
                case "reaction":
                    return "SBO:0000375";
                default:
                    return "SBO:0000000";
            }
        }
    }

    public static ArrayList<String> getValidSchemeAnnotations() {
        ArrayList<String> validAnnotations = new ArrayList<>(Arrays.asList(
                "speciesId", "speciesSboTerm", "speciesType", "speciesInitAmount",
                "speciesCompartment", "speciesDescription", "speciesMetaData", "speciesNote",
                "speciesNetworkId", "speciesPosX", "speciesPosY", "speciesPosZ", "speciesHasOnlySubstanceUnits",
                "speciesBoundaryCondition", "speciesConstant", "speciesMetaId",
                "speciesInitConcentration", "speciesSubstanceUnits", "speciesConversionFactor",
                "reactionId", "reactionType", "reactionName", "reactionSboTerm", "reactionDescription",
                "reactionMetaData", "reactionNote", "reactionNetworkId", "reactionFormula", "reactionMetaId",
                "reactionStartPosX", "reactionStartPosY", "reactionStartPosZ", "reactionEndPosX", "reactionEndPosY",
                "reactionEndPosZ", "reactionIsReversible", "reactionWeight", "reactionReactantsList",
                "reactionProductsList", "reactionCatalystsList", "reactionInhibitorsList", "metadata", "doc"));
        return validAnnotations;
    }

    public static boolean writeTmp(String s, String path) {
        try {
            Files.writeString(Paths.get(path), s);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getFileEnding(String type) {
        switch (type.toLowerCase()) {
            case "xml":
            case "sbml":
                return "xml";
            case "wim":
            case "txt":
                return "txt";
            case "crs":
                return "crs";
            case "db":
                return "db";
            default:
                return "txt";
        }
    }

    public static String toTableDNF(String dnfWCoeff) {
        if (dnfWCoeff.length() > 0) {
            dnfWCoeff = dnfWCoeff.replaceAll("%coeffOf%|%+", " ");

            if (dnfWCoeff.charAt(0) == '{' || dnfWCoeff.charAt(0) == '[') {
                if (dnfWCoeff.charAt(1) == ',' || dnfWCoeff.charAt(1) == '&') {
                    dnfWCoeff = dnfWCoeff.substring(1);
                }
            }
        }
        return dnfWCoeff;
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static double randDouble(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }


    public static double[] getRandomCoords(double minX, double maxX, double minY, double maxY) {
        return new double[]{randDouble(minX, maxX), randDouble(minY, maxY)};
    }

    public static Color getRandomColor() {
        return Color.color(randDouble(0, 1.0), randDouble(0, 1.0), randDouble(0, 1.0), 1);
    }

    public static double[] getAnglesRight(double angleSpan, int index, int count, double length, double xRoot, double yRoot) {
        double minAngle = 180.0 - angleSpan;
        double angle = 90.0;
        if (minAngle < 0.0) {
            minAngle = 0.0;
        }
        double maxAngle = angleSpan;
        if (maxAngle > 180.0) {
            angleSpan = 180.0;
        } else {
            angle = minAngle + index * ((maxAngle - minAngle) * (1.0 / ((double) count - 1.0)));
        }
        double xNew = xRoot + (length * Math.cos(Math.toRadians(angle - 90)));
        double yNew = yRoot + (length * Math.sin(Math.toRadians(angle - 90)));

        return new double[]{xNew, yNew, angle};
    }

    public static double[] getAnglesLeft(double angleSpan, int index, int count, double length, double xRoot, double yRoot) {
        double minAngle = 180.0 - angleSpan;
        double angle = 90.0;
        if (minAngle < 0.0) {
            minAngle = 0.0;
        }
        double maxAngle = angleSpan;
        if (maxAngle > 180.0) {
            angleSpan = 180.0;
        } else {
            angle = minAngle + index * ((maxAngle - minAngle) * (1.0 / ((double) count - 1.0)));
        }
        double xNew = xRoot + (length * Math.cos(Math.toRadians(angle + 90)));
        double yNew = yRoot + (length * Math.sin(Math.toRadians(angle + 90)));

        return new double[]{xNew, yNew, angle};
    }

    public static double[] getAnglesBottom(double angleSpan, int index, int count, double length, double xRoot, double yRoot) {
        double minAngle = 180.0 - angleSpan;
        double angle = 90.0;
        if (minAngle < 0.0) {
            minAngle = 0.0;
        }
        double maxAngle = angleSpan;
        if (maxAngle > 180.0) {
            angleSpan = 180.0;
        } else {
            int c = 0;
            angle = minAngle + index * ((maxAngle - minAngle) * (1.0 / ((double) count - 1.0)));
        }

        double xNew = xRoot + (length * Math.cos(Math.toRadians(angle)));
        double yNew = yRoot + (length * Math.sin(Math.toRadians(angle)));

        return new double[]{xNew, yNew};
    }

    public static double[] getAnglesTop(double angleSpan, int index, int count, double length, double xRoot, double yRoot) {
        double minAngle = 180.0 - angleSpan;
        double angle = 90.0;
        if (minAngle < 0.0) {
            minAngle = 0.0;
        }
        double maxAngle = angleSpan;
        if (maxAngle > 180.0) {
            angleSpan = 180.0;
        } else {
            int c = 0;
            angle = minAngle + index * ((maxAngle - minAngle) * (1.0 / ((double) count - 1.0)));
        }

        double xNew = xRoot + (length * Math.cos(Math.toRadians(angle - 180)));
        double yNew = yRoot + (length * Math.sin(Math.toRadians(angle - 180)));

        return new double[]{xNew, yNew};
    }

    public static double getAngleSpan(int grSize) {
        switch (grSize) {
            case 1:
                return 90;
            case 2:
                return 110;
            case 3:
                return 110;
            case 4:
                return 120;
            case 5:
                return 135;
            case 6:
                return 150;
            case 7:
                return 160;
            default:
                return 170;
        }
    }

    public static int getMiddle(int size) {
        if (size % 2 == 0) {
            return size / 2;
        } else {
            return (size + 1) / 2;
        }
    }

    public static void savePaneSnapshotToFile(VBox vbox, File file) {
        try {
            WritableImage wi = new WritableImage((int) vbox.getWidth(), (int) vbox.getHeight());
            WritableImage snapshot = vbox.snapshot(new SnapshotParameters(), wi);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
