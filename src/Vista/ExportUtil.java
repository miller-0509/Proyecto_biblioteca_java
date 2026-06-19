package Vista;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExportUtil {

    private static String limpiar(String s) {
        if (s == null) return "";
        return s.replace("\n", " ")
                .replace("(", "[").replace(")", "]")
                .replace("\\", "-")
                .replace("\u00d1", "N").replace("\u00f1", "n")
                .replace("\u00e1", "a").replace("\u00e9", "e")
                .replace("\u00ed", "i").replace("\u00f3", "o")
                .replace("\u00fa", "u").replace("\u00fc", "u")
                .replace("\u00c1", "A").replace("\u00c9", "E")
                .replace("\u00cd", "I").replace("\u00d3", "O")
                .replace("\u00da", "U").replace("\u00dc", "U")
                .replace("\u00b0", "o");
    }

    // ── CSV EXPORT (two tables) ──
    public static void exportarCSV(JTable tablaEquipos, JTable tablaLibros,
                                    DefaultTableModel modeloEquipos, DefaultTableModel modeloLibros,
                                    String tituloEquipos, String tituloLibros,
                                    Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte.csv"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()), true)) {
                if (modeloEquipos.getRowCount() > 0) {
                    pw.println(tituloEquipos);
                    for (int c = 0; c < modeloEquipos.getColumnCount(); c++) {
                        if (c > 0) pw.print(",");
                        pw.print(modeloEquipos.getColumnName(c));
                    }
                    pw.println();
                    for (int i = 0; i < modeloEquipos.getRowCount(); i++) {
                        for (int j = 0; j < modeloEquipos.getColumnCount(); j++) {
                            Object val = modeloEquipos.getValueAt(i, j);
                            String s = val != null ? val.toString().replace("\n", " ").replace(",", ";") : "";
                            if (j > 0) pw.print(",");
                            pw.print("\"" + s + "\"");
                        }
                        pw.println();
                    }
                    pw.println();
                }
                if (modeloLibros.getRowCount() > 0) {
                    pw.println(tituloLibros);
                    for (int c = 0; c < modeloLibros.getColumnCount(); c++) {
                        if (c > 0) pw.print(",");
                        pw.print(modeloLibros.getColumnName(c));
                    }
                    pw.println();
                    for (int i = 0; i < modeloLibros.getRowCount(); i++) {
                        for (int j = 0; j < modeloLibros.getColumnCount(); j++) {
                            Object val = modeloLibros.getValueAt(i, j);
                            String s = val != null ? val.toString().replace("\n", " ").replace(",", ";") : "";
                            if (j > 0) pw.print(",");
                            pw.print("\"" + s + "\"");
                        }
                        pw.println();
                    }
                }
                JOptionPane.showMessageDialog(parent, "Archivo CSV exportado correctamente:\n" + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── CSV EXPORT (single table) ──
    public static void exportarCSVSimple(DefaultTableModel modelo, String titulo, Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte.csv"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()), true)) {
                pw.println(titulo);
                for (int c = 0; c < modelo.getColumnCount(); c++) {
                    if (c > 0) pw.print(",");
                    pw.print(modelo.getColumnName(c));
                }
                pw.println();
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    for (int j = 0; j < modelo.getColumnCount(); j++) {
                        Object val = modelo.getValueAt(i, j);
                        String s = val != null ? val.toString().replace("\n", " ").replace(",", ";") : "";
                        if (j > 0) pw.print(",");
                        pw.print("\"" + s + "\"");
                    }
                    pw.println();
                }
                JOptionPane.showMessageDialog(parent, "Archivo CSV exportado correctamente:\n" + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── PDF EXPORT (two tables) ──
    public static void exportarPDF(DefaultTableModel modeloEquipos, DefaultTableModel modeloLibros,
                                    String titulo, String subtitulo, Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte.pdf"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] pdf = generarPDF(modeloEquipos, modeloLibros, titulo, subtitulo);
                FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());
                fos.write(pdf);
                fos.close();
                JOptionPane.showMessageDialog(parent, "Archivo PDF descargado correctamente:\n" + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── PDF EXPORT (single table) ──
    public static void exportarPDFSimple(DefaultTableModel modelo, String titulo, String subtitulo, Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte.pdf"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] pdf = generarPDF(modelo, new DefaultTableModel(), titulo, subtitulo);
                FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());
                fos.write(pdf);
                fos.close();
                JOptionPane.showMessageDialog(parent, "Archivo PDF descargado correctamente:\n" + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PDF GENERATOR — uses Tm (absolute positioning) instead of Td
    // ══════════════════════════════════════════════════════════════════════
    private static byte[] generarPDF(DefaultTableModel mEq, DefaultTableModel mLb, String titulo, String sub) {
        try {
            float pageW = 595;
            float pageH = 842;
            float marginLeft = 40;
            float marginRight = 40;
            float usableW = pageW - marginLeft - marginRight;

            StringBuilder content = new StringBuilder();

            // ── Title ──
            textAt(content, 18, limpiar(titulo), marginLeft, 790);

            // ── Subtitle ──
            textAt(content, 11, limpiar(sub), marginLeft, 770);

            // ── Date ──
            String fecha = "Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
            textAt(content, 9, limpiar(fecha), marginLeft, 755);

            float y = 730;

            // ── Table 1: Equipment ──
            if (mEq.getRowCount() > 0) {
                y = drawPDFTable(content, mEq, "EQUIPOS (" + mEq.getRowCount() + " registros)",
                        marginLeft, y, usableW, pageH, pageW);
            }

            // ── Table 2: Books ──
            if (mLb.getRowCount() > 0) {
                y -= 20;
                if (y < 120) y = 120;
                y = drawPDFTable(content, mLb, "LIBROS (" + mLb.getRowCount() + " registros)",
                        marginLeft, y, usableW, pageH, pageW);
            }

            // ── Build PDF file ──
            byte[] streamBytes = content.toString().getBytes("ISO-8859-1");

            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(pdfOut);

            int objCount = 5;
            int catalogId = 1;
            int pagesId = 2;
            int pageId = 3;
            int contentId = 4;
            int fontId = 5;

            int[] objOffsets = new int[objCount + 1];

            // header
            dos.writeBytes("%PDF-1.4\n");

            // obj 1: catalog
            objOffsets[1] = pdfOut.size();
            dos.writeBytes(catalogId + " 0 obj\n");
            dos.writeBytes("<< /Type /Catalog /Pages " + pagesId + " 0 R >>\n");
            dos.writeBytes("endobj\n");

            // obj 2: pages
            objOffsets[2] = pdfOut.size();
            dos.writeBytes(pagesId + " 0 obj\n");
            dos.writeBytes("<< /Type /Pages /Kids [" + pageId + " 0 R] /Count 1 >>\n");
            dos.writeBytes("endobj\n");

            // obj 3: page
            objOffsets[3] = pdfOut.size();
            dos.writeBytes(pageId + " 0 obj\n");
            dos.writeBytes("<< /Type /Page /Parent " + pagesId + " 0 R");
            dos.writeBytes(" /MediaBox [0 0 " + (int)pageW + " " + (int)pageH + "]");
            dos.writeBytes(" /Contents " + contentId + " 0 R");
            dos.writeBytes(" /Resources << /Font << /F1 " + fontId + " 0 R >> >>");
            dos.writeBytes(" >>\n");
            dos.writeBytes("endobj\n");

            // obj 4: content stream
            objOffsets[4] = pdfOut.size();
            dos.writeBytes(contentId + " 0 obj\n");
            dos.writeBytes("<< /Length " + streamBytes.length + " >>\n");
            dos.writeBytes("stream\n");
            dos.write(streamBytes);
            dos.writeBytes("\nendstream\n");
            dos.writeBytes("endobj\n");

            // obj 5: font
            objOffsets[5] = pdfOut.size();
            dos.writeBytes(fontId + " 0 obj\n");
            dos.writeBytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n");
            dos.writeBytes("endobj\n");

            // xref
            int xrefOffset = pdfOut.size();
            dos.writeBytes("xref\n");
            dos.writeBytes("0 " + (objCount + 1) + "\n");
            dos.writeBytes("0000000000 65535 f \n");
            for (int i = 1; i <= objCount; i++) {
                dos.writeBytes(String.format("%010d 00000 n \n", objOffsets[i]));
            }

            // trailer
            dos.writeBytes("trailer\n");
            dos.writeBytes("<< /Size " + (objCount + 1) + " /Root " + catalogId + " 0 R >>\n");
            dos.writeBytes("startxref\n");
            dos.writeBytes(xrefOffset + "\n");
            dos.writeBytes("%%EOF\n");

            dos.flush();
            return pdfOut.toByteArray();

        } catch (Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    // ── Text positioning using Tm (absolute coordinates) ──
    private static void textAt(StringBuilder sb, float fontSize, String text, float x, float y) {
        sb.append("BT\n");
        sb.append("/F1 ").append(fontSize).append(" Tf\n");
        sb.append("1 0 0 1 ").append((int)x).append(" ").append((int)y).append(" Tm\n");
        sb.append("(").append(text).append(") Tj\n");
        sb.append("ET\n");
    }

    // ── Draw a full table with proper column widths ──
    private static float drawPDFTable(StringBuilder content, DefaultTableModel model, String header,
                                       float x, float startY, float width, float pageH, float pageW) {
        int cols = model.getColumnCount();
        float[] colWidths = new float[cols];
        float totalRatio = 0;
        for (int c = 0; c < cols; c++) {
            colWidths[c] = model.getColumnName(c).length() + 4;
            totalRatio += colWidths[c];
        }
        for (int c = 0; c < cols; c++) {
            colWidths[c] = (colWidths[c] / totalRatio) * width;
        }

        float y = startY;

        // ── Section header ──
        textAt(content, 12, limpiar(header), x, y);
        y -= 8;

        // ── Header line ──
        content.append("0.2 0.2 0.2 RG\n");
        content.append((int)x).append(" ").append((int)y).append(" m ");
        content.append((int)(x + width)).append(" ").append((int)y).append(" l S\n");
        y -= 3;

        // ── Column headers ──
        float cx = x;
        for (int c = 0; c < cols; c++) {
            String colName = limpiar(model.getColumnName(c));
            if (colName.length() > 15) colName = colName.substring(0, 15);
            textAt(content, 8, colName, cx + 3, y);
            cx += colWidths[c];
        }
        y -= 14;

        // ── Separator line under headers ──
        content.append("0.7 0.7 0.7 RG\n");
        content.append((int)x).append(" ").append((int)y).append(" m ");
        content.append((int)(x + width)).append(" ").append((int)y).append(" l S\n");
        y -= 6;

        // ── Data rows ──
        for (int i = 0; i < model.getRowCount(); i++) {
            if (y < 60) {
                textAt(content, 8, "... continúa en la siguiente pagina ...", x, 40);
                break;
            }

            // Alternating row background
            if (i % 2 == 1) {
                content.append("0.96 0.96 0.97 rg\n");
                content.append((int)x).append(" ").append((int)(y - 2)).append(" m ");
                content.append((int)(x + width)).append(" ").append((int)(y - 2)).append(" l ");
                content.append((int)(x + width)).append(" ").append((int)(y + 12)).append(" l ");
                content.append((int)x).append(" ").append((int)(y + 12)).append(" l ");
                content.append("f\n");
                content.append("0 0 0 rg\n");
            }

            float cx2 = x;
            for (int c = 0; c < cols; c++) {
                Object val = model.getValueAt(i, c);
                String s = limpiar(val != null ? val.toString() : "");
                int maxChars = (int)(colWidths[c] / 4.5);
                if (maxChars < 3) maxChars = 3;
                if (s.length() > maxChars) s = s.substring(0, maxChars) + "..";
                textAt(content, 8, s, cx2 + 3, y);
                cx2 += colWidths[c];
            }

            // Row bottom line
            content.append("0.9 0.9 0.91 RG\n");
            content.append((int)x).append(" ").append((int)(y - 4)).append(" m ");
            content.append((int)(x + width)).append(" ").append((int)(y - 4)).append(" l S\n");

            y -= 16;
        }

        return y;
    }
}
