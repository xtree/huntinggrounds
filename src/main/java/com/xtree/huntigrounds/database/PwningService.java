package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.Pwning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pwningService")
public class PwningService {

    private PwningRepository pwningRepository;

    @Autowired
    public PwningService(PwningRepository pwningRepository) {
        this.pwningRepository = pwningRepository;
    }

    public void savePwning(Pwning pwning) {
        pwningRepository.save(pwning);
    }

    public void delete(Pwning pwning) {
        pwningRepository.delete(pwning);
    }
}