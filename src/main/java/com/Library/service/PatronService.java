package com.Library.service;import com.Library.common.ValidationException;import com.Library.dtos.BookDTO;import com.Library.dtos.PatronDTO;import com.Library.dtos.mapper.PatronMapper;import com.Library.models.Patron;import com.Library.models.repositories.PatronRepository;import jakarta.transaction.Transactional;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.cache.annotation.CacheEvict;import org.springframework.cache.annotation.CachePut;import org.springframework.cache.annotation.Cacheable;import org.springframework.stereotype.Service;import reactor.core.publisher.Flux;import java.util.ArrayList;import java.util.List;import java.util.NoSuchElementException;import java.util.Optional;@Servicepublic class PatronService {  private final PatronRepository  patronRepository;  private final PatronMapper patronMapper;  public PatronService(@Autowired PatronRepository patronRepository, @Autowired PatronMapper patronMapper){    this.patronRepository= patronRepository;    this.patronMapper = patronMapper;  }  public Flux<PatronDTO> findAllPatrons(){    List<Patron> patronsEntity= patronRepository.findAll();    List<PatronDTO> DTOs= new ArrayList<>();    for ( Patron patron : patronsEntity){      DTOs.add(patronMapper.entity_TO_DTO(patron));    }    return Flux.fromIterable(DTOs);  }  @Cacheable("patrons")  public PatronDTO findOnePatron(Long patronId) throws Exception {    try    {      return patronMapper.entity_TO_DTO(patronRepository.findById(patronId).get());    } catch (NoSuchElementException e){      throw new ValidationException("No patrons be founded matches your entered id  >>  "+ e.getMessage()) ;    }  }  @Transactional(rollbackOn = ValidationException.class)  public PatronDTO saveOnePatron(PatronDTO patronDTO) throws ValidationException {    try{      return patronMapper.entity_TO_DTO( patronRepository.save(patronMapper.DTO_TO_Entity(patronDTO)) );    }catch(Exception e){      throw new ValidationException("Error exists while saving the new patrons >>  " + e.getMessage());    }  }  @Transactional(rollbackOn = ValidationException.class)  @CachePut("patrons")  public PatronDTO editSinglePatron(PatronDTO patronDTO, Long patronId ) throws Exception {    PatronDTO pattronResponse=  findOnePatron(patronId);    if(pattronResponse!=null) {      pattronResponse.setName(patronDTO.getName());      pattronResponse.setId(patronDTO.getId());      pattronResponse.setContactInformation(patronDTO.getContactInformation());      saveOnePatron (pattronResponse);      return pattronResponse;    }else{      throw new ValidationException("No patrons be founded matches your entered id  ") ;    }  }  @Transactional(rollbackOn = ValidationException.class)  @CacheEvict("patrons")  public void deleteSinglePatron(Long patronId) throws Exception {    try    {      if(findOnePatron(patronId) != null )  patronRepository.deleteById(patronId);    }catch (Exception e){      throw new ValidationException("No patrons be founded matches your entered id   >>   "+ e.getMessage());    }  }  public Patron DTO_TO_Entity(PatronDTO patronDTO){      Patron entity = new Patron();      entity.setContactInformation(patronDTO.getContactInformation());      entity.setName(patronDTO.getName());      entity.setId(patronDTO.getId());      return entity;  }  public PatronDTO Entity_TO_DTO(Patron patron){    PatronDTO patronDTO = new PatronDTO();    patronDTO.setContactInformation(patron.getContactInformation());    patronDTO.setName(patron.getName());    patronDTO.setId(patron.getId());    return patronDTO;  }}