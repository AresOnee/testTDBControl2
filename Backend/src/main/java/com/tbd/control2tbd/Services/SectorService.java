package com.tbd.control2tbd.Services;

import com.tbd.control2tbd.DTOs.SectorDTO;
import com.tbd.control2tbd.Entities.Sector;
import com.tbd.control2tbd.Repositories.SectorRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    public List<SectorDTO> obtenerSectores() {
        List<Sector> sectores = sectorRepository.findAll();

        // Convertir la lista de sectores a DTOs
        return sectores.stream()
                .map(sector -> {
                    SectorDTO dto = new SectorDTO();
                    dto.setId(sector.getId());
                    dto.setNombre(sector.getNombre());
                    dto.setTipo(sector.getTipo());
                    dto.setLatitud(sector.getUbicacion().getY()); // latitud
                    dto.setLongitud(sector.getUbicacion().getX()); // longitud
                    return dto;
                })
                .collect(Collectors.toList());
    }



    public Sector crearSector(SectorDTO dto) {
        Sector sector = new Sector();
        sector.setNombre(dto.getNombre());
        sector.setTipo(dto.getTipo());

        Point punto = geometryFactory.createPoint(new Coordinate(dto.getLongitud(), dto.getLatitud()));
        sector.setUbicacion(punto);

        return sectorRepository.save(sector);
    }
}
