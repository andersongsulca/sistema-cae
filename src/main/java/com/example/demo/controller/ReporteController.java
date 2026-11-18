package com.example.demo.controller;

import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.MovimientoInventario;
import com.example.demo.service.ProductoService;
import com.example.demo.service.MovimientoInventarioService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasAnyAuthority('ROLE_DIRECTORA', 'ROLE_SECRETARIA')")
public class ReporteController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private MovimientoInventarioService movimientoService;

    @GetMapping
    public String mostrarMenuReportes() {
        return "reportes/index";
    }

    // REPORTE DE INVENTARIO - GENERA PDF
    @GetMapping("/inventario/pdf")
    public void generarReporteInventarioPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_inventario.pdf");
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        
        Paragraph titulo = new Paragraph("REPORTE DE INVENTARIO", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);
        
        Paragraph institucion = new Paragraph("I.E. Inicial \"11 de Junio\" - CAE", subtituloFont);
        institucion.setAlignment(Element.ALIGN_CENTER);
        institucion.setSpacingAfter(5f);
        document.add(institucion);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph fecha = new Paragraph("Fecha: " + LocalDate.now().format(formatter), normalFont);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingAfter(20f);
        document.add(fecha);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        float[] columnWidths = {1f, 3f, 2f, 1.5f, 1.5f, 1.5f};
        table.setWidths(columnWidths);
        
        PdfPCell header;
        
        header = new PdfPCell(new Phrase("Código", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Producto", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Categoría", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Stock", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Mínimo", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Estado", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        List<Producto> productos = productoService.listarTodos();
        int totalProductos = productos.size();
        int conStock = 0;
        int stockBajo = 0;
        int agotados = 0;
        
        for (Producto p : productos) {
            table.addCell(new Phrase(p.getCodigo() != null ? p.getCodigo() : "", normalFont));
            table.addCell(new Phrase(p.getNombre() != null ? p.getNombre() : "", normalFont));
            table.addCell(new Phrase(p.getCategoria() != null ? p.getCategoria() : "", normalFont));
            
            PdfPCell cellStock = new PdfPCell(new Phrase(String.valueOf(p.getStockActual()), normalFont));
            cellStock.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellStock);
            
            PdfPCell cellMinimo = new PdfPCell(new Phrase(String.valueOf(p.getStockMinimo()), normalFont));
            cellMinimo.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellMinimo);
            
            String estado = p.getEstado() != null ? p.getEstado().name() : "ACTIVO";
            PdfPCell cellEstado = new PdfPCell(new Phrase(estado, normalFont));
            cellEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            if (p.getStockActual().doubleValue() == 0) {
                cellEstado.setBackgroundColor(Color.RED);
                agotados++;
            } else if (p.getStockActual().doubleValue() <= Double.parseDouble(String.valueOf(p.getStockMinimo()))) {
                cellEstado.setBackgroundColor(Color.ORANGE);
                stockBajo++;
            } else {
                conStock++;
            }
            
            table.addCell(cellEstado);
        }
        
        document.add(table);
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph("ESTADÍSTICAS:", subtituloFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total Productos: " + totalProductos, normalFont));
        document.add(new Paragraph("Con Stock Adecuado: " + conStock, normalFont));
        document.add(new Paragraph("Stock Bajo: " + stockBajo, normalFont));
        document.add(new Paragraph("Agotados: " + agotados, normalFont));
        
        document.close();
    }

    // REPORTE DE MOVIMIENTOS - GENERA PDF
    @GetMapping("/movimientos/pdf")
    public void generarReporteMovimientosPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_movimientos.pdf");
        
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        
        Paragraph titulo = new Paragraph("REPORTE DE MOVIMIENTOS", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Paragraph fecha = new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingAfter(20f);
        document.add(fecha);
        
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        
        float[] columnWidths = {2f, 2f, 3f, 1.5f, 2f, 2f, 2f};
        table.setWidths(columnWidths);
        
        PdfPCell header;
        header = new PdfPCell(new Phrase("Fecha", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Tipo", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Producto", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Cantidad", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Stock Anterior", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Stock Nuevo", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Usuario", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(header);
        
        // OBTENER MOVIMIENTOS REALES
        List<MovimientoInventario> movimientos = movimientoService.listarTodos();
        
        if (movimientos.isEmpty()) {
            PdfPCell celdaVacia = new PdfPCell(new Phrase("No hay movimientos registrados", normalFont));
            celdaVacia.setColspan(7);
            celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaVacia.setPadding(10);
            table.addCell(celdaVacia);
        } else {
            for (MovimientoInventario m : movimientos) {
                // Fecha
                String fechaStr = m.getFechaMovimiento() != null ? 
                    m.getFechaMovimiento().format(formatter) : "N/A";
                table.addCell(new Phrase(fechaStr, normalFont));
                
                // Tipo
                String tipo = m.getTipo() != null ? m.getTipo().name() : "N/A";
                table.addCell(new Phrase(tipo, normalFont));
                
                // Producto
                String producto = (m.getProducto() != null && m.getProducto().getNombre() != null) ? 
                    m.getProducto().getNombre() : "N/A";
                table.addCell(new Phrase(producto, normalFont));
                
                // Cantidad
                String cantidad = m.getCantidad() != null ? String.valueOf(m.getCantidad()) : "0";
                table.addCell(new Phrase(cantidad, normalFont));
                
                // Stock Anterior
                String stockAnt = m.getStockAnterior() != null ? String.valueOf(m.getStockAnterior()) : "0";
                table.addCell(new Phrase(stockAnt, normalFont));
                
                // Stock Nuevo (CORREGIDO)
                String stockNuevo = m.getStockNuevo() != null ? String.valueOf(m.getStockNuevo()) : "0";
                table.addCell(new Phrase(stockNuevo, normalFont));
                
                // Usuario
                String usuario = (m.getUsuario() != null && m.getUsuario().getNombre() != null) ? 
                    m.getUsuario().getNombre() : "N/A";
                table.addCell(new Phrase(usuario, normalFont));
            }
        }
        
        document.add(table);
        
        // Estadísticas
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total de movimientos: " + movimientos.size(), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        
        document.close();
    }

    // PRODUCTOS CRÍTICOS - GENERA PDF
    @GetMapping("/criticos/pdf")
    public void generarReporteProductosCriticosPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=productos_criticos.pdf");
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        
        Paragraph titulo = new Paragraph("PRODUCTOS CRÍTICOS - STOCK BAJO", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph fecha = new Paragraph("Fecha: " + LocalDate.now().format(formatter), normalFont);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingAfter(20f);
        document.add(fecha);
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        
        float[] columnWidths = {1.5f, 3f, 2f, 2f, 2f};
        table.setWidths(columnWidths);
        
        PdfPCell header;
        header = new PdfPCell(new Phrase("Código", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Producto", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Stock Actual", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Stock Mínimo", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase("Diferencia", headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
        
        List<Producto> productos = productoService.listarTodos();
        int contadorCriticos = 0;
        
        for (Producto p : productos) {
            double stockActual = p.getStockActual().doubleValue();
            double stockMinimo = Double.parseDouble(String.valueOf(p.getStockMinimo()));
            
            if (stockActual <= stockMinimo) {
                double diferencia = stockActual - stockMinimo;
                
                table.addCell(new Phrase(p.getCodigo() != null ? p.getCodigo() : "", normalFont));
                table.addCell(new Phrase(p.getNombre() != null ? p.getNombre() : "", normalFont));
                
                PdfPCell cellActual = new PdfPCell(new Phrase(String.valueOf(p.getStockActual()), normalFont));
                cellActual.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellActual.setBackgroundColor(Color.RED);
                table.addCell(cellActual);
                
                PdfPCell cellMinimo = new PdfPCell(new Phrase(String.valueOf(p.getStockMinimo()), normalFont));
                cellMinimo.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellMinimo);
                
                PdfPCell cellDiferencia = new PdfPCell(new Phrase(String.valueOf(diferencia), normalFont));
                cellDiferencia.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (diferencia < 0) {
                    cellDiferencia.setBackgroundColor(Color.RED);
                }
                table.addCell(cellDiferencia);
                
                contadorCriticos++;
            }
        }
        
        document.add(table);
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total productos críticos: " + contadorCriticos, headerFont));
        
        document.close();
    }
}