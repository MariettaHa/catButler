package catbutler.gui.converter;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.io.documents.DBDoc;
import catbutler.io.documents.SBMLDoc;
import catbutler.io.parser.CRSParser;
import catbutler.io.parser.CustomParser;
import catbutler.io.parser.WimParser;
import catbutler.io.writer.SBMLDocWriter;
import catbutler.utils.Misc;
import org.sbml.jsbml.SBMLError;

import java.io.File;

public class FormatValidator {

    public static StringBuilder errorBuilder = new StringBuilder();

    public static boolean validateFile(File file, File parsingScheme, String type) {
        errorBuilder = new StringBuilder();
        if (file.exists()) {
            switch (type) {
                case "crs":
                    return CRSParser.validateFile(file);
                case "sbml":
                case "xml":
                    SBMLDoc sbmlDoc = new SBMLDoc(file.toPath(), 3, 2);
                    sbmlDoc.readIn();
                    if (sbmlDoc.getDataModel().notEmpty()) {
                        SBMLDocWriter sbmlDocWriter = new SBMLDocWriter(sbmlDoc);
                        if (sbmlDocWriter.getSbmlDocument().getErrorLog().getValidationErrors().size() > 0) {
                            for (SBMLError err : sbmlDocWriter.getSbmlDocument().getErrorLog().getValidationErrors()) {
                                errorBuilder.append(err.getMessage() + "\n");
                            }
                            new QuickInfoPopup("SBML Parsing Error Log", errorBuilder.toString() + "\n(Check SBML Documentation for more Information on all error categories.)", -1, null);
                        }

                        Converter.lastDataModel = sbmlDoc.getDataModel();
                        return true;
                    }
                    return false;
                case "wim":
                case "txt":
                    return WimParser.validateFile(file);
                case "db":
                    return DBDoc.validateFile(file);
                default:
                    if (parsingScheme != null) {
                        if (parsingScheme.exists()) {
                            return CustomParser.validateFile(file, parsingScheme);
                        }
                    }
            }
        } else {
            return false;
        }
        return false;
    }

    public static boolean validateString(String string, File parsingScheme, String type) {
        File file = new File("src/catbutler/resources/utilfiles/tmpFile_validateStr." + Misc.getFileEnding(type));
        if (Misc.writeTmp(string, file.getPath())) {
            return validateFile(file, parsingScheme, type);
        } else {
            return false;
        }
    }


}
