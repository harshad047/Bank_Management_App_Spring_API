package com.tss.bank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappingService {
    
    @Autowired
    private ModelMapper modelMapper;
    
    /**
     * Maps a source object to a destination class
     */
    public <T, U> U map(T source, Class<U> destinationClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationClass);
    }
    
    /**
     * Maps a list of source objects to a list of destination objects
     */
    public <T, U> List<U> mapList(List<T> sourceList, Class<U> destinationClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return List.of();
        }
        return sourceList.stream()
                .map(source -> modelMapper.map(source, destinationClass))
                .collect(Collectors.toList());
    }
    
    /**
     * Maps properties from source to existing destination object
     */
    public <T, U> void mapTo(T source, U destination) {
        if (source != null && destination != null) {
            modelMapper.map(source, destination);
        }
    }
}
