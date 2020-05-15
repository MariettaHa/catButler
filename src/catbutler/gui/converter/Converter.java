package catbutler.gui.converter;

import catbutler.io.documents.CRSDoc;
import catbutler.io.documents.DBDoc;
import catbutler.io.documents.SBMLDoc;
import catbutler.io.documents.WimDoc;
import catbutler.io.parser.CustomParser;
import catbutler.io.writer.CRSWriter;
import catbutler.io.writer.DBWriter;
import catbutler.io.writer.SBMLDocWriter;
import catbutler.io.writer.WimWriter;
import catbutler.model.DataModel;

import java.io.File;
import java.nio.file.Path;

public class Converter {

    public static DataModel lastDataModel;

    public static boolean convert(Path path, String outputPath) {
        String pathStr = path.toString();
        String fromType = pathStr.split("\\.")[pathStr.split("\\.").length - 1];
        String toType = outputPath.split("\\.")[outputPath.split("\\.").length - 1];

        lastDataModel = null;
        switch (fromType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(path);
                crsDoc.readIn();
                lastDataModel = crsDoc.getDataModel();
                break;
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(path, 2, 4);
                sbmlDoc.readIn();
                lastDataModel = sbmlDoc.getDataModel();
                break;
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(path);
                wimDoc.readIn();
                lastDataModel = wimDoc.getDataModel();
                break;
            case "db":
                DBDoc dbDoc = new DBDoc(path);
                dbDoc.readIn();
                lastDataModel = dbDoc.getDataModel();
                break;
            default:
                break;
        }

        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                crsWriter.write(outputPath);
                break;
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.write(outputPath);
                break;
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                wimWriter.write(outputPath);
                break;
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                dbWriter.write(outputPath);
                break;
            default:
                break;
        }
        return false;
    }

    public static String convertToString(Path inputPath, String fromType, String toType) {
        lastDataModel = null;
        switch (fromType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(inputPath);
                crsDoc.readIn();
                lastDataModel = crsDoc.getDataModel();
                break;
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(inputPath, 2, 4);
                sbmlDoc.readIn();
                lastDataModel = sbmlDoc.getDataModel();
                break;
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(inputPath);
                wimDoc.readIn();
                lastDataModel = wimDoc.getDataModel();
                break;
            case "db":
                DBDoc dbDoc = new DBDoc(inputPath);
                dbDoc.readIn();
                lastDataModel = dbDoc.getDataModel();
                break;
            default:
                break;
        }

        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                return crsWriter.buildString();
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.buildSBMLDocument();
                return sbmlDocWriter.getDocAsString();
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                return wimWriter.buildString();
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                return dbWriter.getDBString();
            default:
                break;
        }
        return "";
    }

    public static String convertStringToString(String inputStr, String fromType, String toType) {
        lastDataModel = null;
        switch (fromType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(inputStr);
                crsDoc.readIn(inputStr);
                lastDataModel = crsDoc.getDataModel();
                break;
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(inputStr, 2, 4);
                sbmlDoc.readIn(inputStr);
                lastDataModel = sbmlDoc.getDataModel();
                break;
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(inputStr);
                wimDoc.readIn(inputStr);
                lastDataModel = wimDoc.getDataModel();
            default:
                break;
        }

        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                return crsWriter.buildString();
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.buildSBMLDocument();
                return sbmlDocWriter.getDocAsString();
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                return wimWriter.buildString();
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                return dbWriter.getDBString();
            default:
                break;
        }
        return "";
    }


    public static String convertDataModelTo(DataModel dataModel1, String toType) {
        lastDataModel = dataModel1;
        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                return crsWriter.buildString();
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.buildSBMLDocument();
                return sbmlDocWriter.getDocAsString();
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                return wimWriter.buildString();
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                return dbWriter.getDBString();
            default:
                break;
        }
        return "";
    }

    public static void convertFromCustom(Path inputFile, String outputPath, File schemeFile) {
        String toType = outputPath.split("\\.")[outputPath.split("\\.").length - 1];

        CustomParser customParser = new CustomParser(inputFile.toFile(), schemeFile);
        lastDataModel = customParser.getDataModel();
        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                crsWriter.write(outputPath);
                break;
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.write(outputPath);
                break;
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                wimWriter.write(outputPath);
                break;
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                dbWriter.write(outputPath);
                break;
            default:
                break;
        }

    }


    public static void convertFromCustomString(String inputStr, String outputPath, File schemeFile) {
        String toType = outputPath.split("\\.")[outputPath.split("\\.").length - 1];

        CustomParser customParser = new CustomParser(inputStr, schemeFile);
        lastDataModel = customParser.getDataModel();
        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                crsWriter.write(outputPath);
                break;
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.write(outputPath);
                break;
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                wimWriter.write(outputPath);
                break;
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                dbWriter.write(outputPath);
                break;
            default:
                break;

        }
    }

    public static String convertStringToString(String inputStr, File schemeFile, String fromType, String toType) {
        CustomParser customParser = new CustomParser(inputStr, schemeFile);
        lastDataModel = customParser.getDataModel();

        switch (toType) {
            case "crs":
                CRSDoc crsDoc = new CRSDoc(lastDataModel);
                CRSWriter crsWriter = new CRSWriter(crsDoc);
                return crsWriter.buildString();
            case "sbml":
            case "xml":
                SBMLDoc sbmlDoc = new SBMLDoc(lastDataModel);
                SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                sbmlDocWriter.buildSBMLDocument();
                return sbmlDocWriter.getDocAsString();
            case "wim":
            case "txt":
                WimDoc wimDoc = new WimDoc(lastDataModel);
                WimWriter wimWriter = new WimWriter(wimDoc);
                return wimWriter.buildString();
            case "db":
                DBDoc dbDoc = new DBDoc(lastDataModel);
                DBWriter dbWriter = new DBWriter(dbDoc);
                return dbWriter.getDBString();
            default:
                break;
        }
        return "";
    }


}
