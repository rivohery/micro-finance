package com.alibou.finance.account.infrastructure.adapter.out.service.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class PdfFileStyle {
    // Définition de la palette de couleurs (Professionnelle, Tons Bleu Marine/Slate)
    protected static final BaseColor COLOR_PRIMARY = new BaseColor(26, 54, 93);    // #1A365D - Bleu Marine Sombre
    protected static final BaseColor COLOR_SECONDARY = new BaseColor(43, 108, 176); // #2B6CB0 - Bleu Accent
    protected static final BaseColor COLOR_ROW_EVEN = new BaseColor(247, 250, 252); // #F7FAFC - Gris très clair pour l'alternance
    protected static final BaseColor COLOR_TEXT = new BaseColor(45, 55, 72);       // #2D3748 - Texte sombre doux (pas de noir pur)
    protected static final BaseColor COLOR_BORDER = new BaseColor(226, 232, 240);   // #E2E8F0 - Lignes de bordures discrètes

    // Formatiage pour les données financières et les dates
    protected static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Méthode utilitaire pour générer des cellules uniformes, aérées avec de jolies bordures fines
    protected static PdfPCell createDataCell(String text, Font font, BaseColor bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(7);
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(COLOR_BORDER); // Bordure fine grise discrète
        cell.setBorderWidth(0.5f);
        return cell;
    }
}
