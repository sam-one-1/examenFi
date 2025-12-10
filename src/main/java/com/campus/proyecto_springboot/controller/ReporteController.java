package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.dto.ResumenGeneralDTO;
import com.campus.proyecto_springboot.service.Reporte.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/resumen")
    public ResponseEntity<ResumenGeneralDTO> obtenerResumen() {
        ResumenGeneralDTO resumen = reporteService.obtenerResumenGeneral();
        return ResponseEntity.ok(resumen);
    }
}
