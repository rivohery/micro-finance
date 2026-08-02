package com.alibou.finance.account.infrastructure.adapter.out.service.pdf;

import com.alibou.finance.account.infrastructure.adapter.in.dto.TransactionResponse;
import org.springframework.stereotype.Component;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionPdfReport extends PdfFileStyle {
    public byte[] reportTransactionsToPdf(List<TransactionResponse> transactions, LocalDate createdDate) {
        // Étape 1 : Créer le document en paysage (A4, Landscape) car il y a beaucoup de colonnes
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Étape 2 : Ajouter un pied de page automatique (Numérotation de page)
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

                    // Texte à gauche
                    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                            new Phrase("Application Micro-finance Hexagonale — Rapport d'Audit Interne", fontFooter),
                            document.left(), document.bottom() - 15, 0);

                    // Numéro de page à droite
                    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                            new Phrase(String.format("Page %d", writer.getPageNumber()), fontFooter),
                            document.right(), document.bottom() - 15, 0);
                }
            });

            document.open();

            // ----------------------------------------------------
            // Étape 3 : LE BLOC D'ENTÊTE (Titre & Métadonnées en haut à droite)
            // ----------------------------------------------------
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60f, 40f}); // 60% Gauche (Logo/App), 40% Droite (Infos)

            // Cellule Gauche : Nom de l'application
            Font appFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_PRIMARY);
            Font subAppFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);
            Paragraph leftHeader = new Paragraph();
            leftHeader.add(new Chunk("HEXAFINANCE APP\n", appFont));
            leftHeader.add(new Chunk("Système de Micro-finance & Inclusion Financière", subAppFont));

            PdfPCell leftCell = new PdfPCell(leftHeader);
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            headerTable.addCell(leftCell);

            // Cellule Droite : Bloc Titre + Date stylisé (Comme demandé en haut à droite)
            PdfPTable infoBlock = new PdfPTable(1);
            infoBlock.setWidthPercentage(100);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, COLOR_TEXT);

            // Sous-cellule Titre avec un fond coloré
            PdfPCell titleCell = new PdfPCell(new Phrase("Liste des Transactions", titleFont));
            titleCell.setBackgroundColor(COLOR_PRIMARY);
            titleCell.setPadding(6);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setBorder(Rectangle.NO_BORDER);
            infoBlock.addCell(titleCell);

            // Sous-cellule Date
            PdfPCell dateCell = new PdfPCell(new Phrase("Date du jour : " + createdDate.atStartOfDay().format(DATE_FORMATTER), dateFont));
            dateCell.setBackgroundColor(COLOR_ROW_EVEN);
            dateCell.setPadding(6);
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setBorder(Rectangle.BOX);
            dateCell.setBorderColor(COLOR_BORDER);
            infoBlock.addCell(dateCell);

            PdfPCell rightCell = new PdfPCell(infoBlock);
            rightCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(rightCell);

            document.add(headerTable);

            // Ligne de séparation ou espace
            document.add(new Paragraph("\n"));

            // ----------------------------------------------------
            // Étape 4 : LE TABLEAU DES TRANSACTIONS
            // ----------------------------------------------------
            // 8 colonnes définies
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            // Ajustement proportionnel de la largeur des colonnes (Total = 100)
            table.setWidths(new float[]{14f, 15f, 13f, 8f, 8f, 14f, 12f, 16f});

            // Polices pour le tableau
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, BaseColor.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, COLOR_TEXT);

            // Définition des entêtes (Plus propres, abrégés mais clairs)
            String[] headers = {
                    "N° Compte Concerné",
                    "Référence Trans.",
                    "Montant Initial",
                    "Devise T.",
                    "Devise C.",
                    "Montant Final",
                    "Taux de Change",
                    "Date & Heure"
            };

            // Ajout des cellules d'entête
            for (String headerText : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(headerText, headFont));
                cell.setBackgroundColor(COLOR_PRIMARY);
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderColor(COLOR_PRIMARY); // Cache les bordures internes de l'entête
                table.addCell(cell);
            }

            // Ajout des lignes de données
            int rowCount = 0;
            for (TransactionResponse tx : transactions) {
                boolean isEven = (rowCount % 2 == 0);
                BaseColor currentBg = isEven ? COLOR_ROW_EVEN : BaseColor.WHITE;

                // 1. Numéro du compte
                table.addCell(createDataCell(tx.getAccountNumber(), dataFont, currentBg, Element.ALIGN_CENTER));

                // 2. Référence de la transaction
                table.addCell(createDataCell(tx.getReference(), dataFont, currentBg, Element.ALIGN_CENTER));

                // 3. Montant initial
                String initialAmtStr = MONEY_FORMAT.format(tx.getOriginalAmount());
                table.addCell(createDataCell(initialAmtStr, dataFont, currentBg, Element.ALIGN_RIGHT));

                // 4. Monnaie de la transaction
                table.addCell(createDataCell(tx.getTransactionCurrency(), dataFont, currentBg, Element.ALIGN_CENTER));

                // 5. Monnaie du compte
                table.addCell(createDataCell(tx.getTargetCurrency(), dataFont, currentBg, Element.ALIGN_CENTER));

                // 6. Montant final (après conversion)
                String finalAmtStr = MONEY_FORMAT.format(tx.getFinalAmount());
                table.addCell(createDataCell(finalAmtStr, dataFont, currentBg, Element.ALIGN_RIGHT));

                // 7. Taux de change
                String rateStr = MONEY_FORMAT.format(tx.getExchangeRate());
                table.addCell(createDataCell(rateStr, dataFont, currentBg, Element.ALIGN_RIGHT));

                // 8. Date de la transaction
                String dateStr = tx.getCreatedDate().format(DATE_FORMATTER);
                table.addCell(createDataCell(dateStr, dataFont, currentBg, Element.ALIGN_CENTER));

                rowCount++;
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }


}
