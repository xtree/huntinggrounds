package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.Spot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("spotService")
public class SpotService {

    private SpotRepository spotRepository;

    @Autowired
    public SpotService(SpotRepository spotRepository
                      ) {
        this.spotRepository = spotRepository;
    }

    public void saveSpot(Spot spot) {
        spotRepository.save(spot);
    }

    public List<Spot> allSpots(){
        return spotRepository.findAll();
    }

    public Spot getSpot(String code) {
        return spotRepository.findByCode(code);
    }

    public void modifySpot(int might, String place, String description, String code){
        spotRepository.setPlaceDetailsByCode(might,place,description,code);
    }
}