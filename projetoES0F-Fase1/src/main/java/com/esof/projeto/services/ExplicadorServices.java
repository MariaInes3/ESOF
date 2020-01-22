package com.esof.projeto.services;

import com.esof.projeto.models.Cadeira;
import com.esof.projeto.models.Curso;
import com.esof.projeto.models.Disponibilidade;
import com.esof.projeto.models.Explicador;
import com.esof.projeto.repositories.CursoRepo;
import com.esof.projeto.repositories.DisponibilidadeRepo;
import com.esof.projeto.repositories.ExplicadorRepo;
import com.esof.projeto.services.filters.explicador.FilterExplicadorObject;
import com.esof.projeto.services.filters.explicador.FilterExplicadorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ExplicadorServices {

    private ExplicadorRepo explicadorRepo;
    private CursoRepo cursoRepo;
    private DisponibilidadeRepo disponibilidadeRepo;

    @Autowired
    private FilterExplicadorServices filterService;

    @Autowired
    public ExplicadorServices(ExplicadorRepo explicadorRepo, CursoRepo cursoRepo) {
        this.cursoRepo = cursoRepo;
        this.explicadorRepo = explicadorRepo;
    }

    public Optional<Explicador> createExplicador(Explicador explicador) {
        Optional<Explicador> optionalExplicador = this.explicadorRepo.findByName(explicador.getName());
        if (optionalExplicador.isPresent()) {
            return Optional.empty();
        }
        Explicador createdExplicador = this.explicadorRepo.save(explicador);
        return Optional.of(createdExplicador);
    }

    public Set<Explicador> findAll() {
        Set<Explicador> explicadors = new HashSet<>();
        for (Explicador explicador : this.explicadorRepo.findAll()) {
            explicadors.add(explicador);
        }
        return explicadors;
    }

    public Optional<Explicador> findById(Long id) {
        return this.explicadorRepo.findById(id);
    }

    public Optional<Explicador> findByName(String name) {
        return this.explicadorRepo.findByName(name);
    }

    public Optional<Explicador> putExplicaCurso(String curso, Explicador explicador) {

        if (curso != null) {
            Optional<Curso> optionalCurso = cursoRepo.findByName(curso);
            if (optionalCurso.isPresent()) {
                Optional<Explicador> optionalExplicador = explicadorRepo.findById(explicador.getId());
                if (optionalExplicador.isPresent()) {
                    Set<Cadeira> cadeiras = optionalCurso.get().getCadeiras();
                    optionalExplicador.get().setCadeiras(cadeiras);

                    return optionalExplicador;

                }
            }
        }
        return Optional.empty();
    }

    public Optional<Explicador> putExplicadorDisponibilidade(Disponibilidade disponibilidade, Explicador explicador) {

        if (disponibilidade != null) {
            Optional<Disponibilidade> disponibilidadeOptional = disponibilidadeRepo.findById(disponibilidade.getId());
            if (disponibilidadeOptional.isPresent()) {
                Optional<Explicador> optionalExplicador = explicadorRepo.findById(explicador.getId());
                if (optionalExplicador.isPresent()) {
                    optionalExplicador.get().addDisponibilidade(disponibilidade);

                    return optionalExplicador;

                }
            }
        }
        return Optional.empty();
    }

    public Optional<Explicador> updateCurso(Long id, Explicador explicador) {
        if (id != null) {
            Optional<Curso> optionalCurso = cursoRepo.findById(id);
            if (optionalCurso.isPresent()) {
                Optional<Explicador> optionalExplicador = explicadorRepo.findById(explicador.getId());
                if (optionalExplicador.isPresent()) {
                    Set<Cadeira> setCadeirasexplicador = optionalExplicador.get().getCadeiras();
                    Set<Cadeira> setCadeirascurso = optionalCurso.get().getCadeiras();
                    for (Cadeira ce : setCadeirasexplicador) {
                        for (Cadeira cc : setCadeirascurso) {
                            if (!ce.getId().equals(cc.getId())) {
                                optionalExplicador.get().addCadeira(cc);
                            }
                        }
                    }
                    return optionalExplicador;
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Explicador> updateDisciplina(Explicador explicador) {
        if (explicador.getDisponibilidades() != null) {
                Set<Disponibilidade> disponibilidades = explicador.getDisponibilidades();
                if (explicador.getName() != null) {
                    Optional<Explicador> explicadorOptional = explicadorRepo.findByName(explicador.getName());
                    if (explicadorOptional.isPresent()) {
                        for (Disponibilidade dd : disponibilidades) {
                            if(!dd.getInicio().isAfter(dd.getFim())){
                                explicadorOptional.get().addDisponibilidade(dd);
                            }
                        }
                        return explicadorOptional;
                    }
                }
            }
        return Optional.empty();
    }

    public Set<Explicador> filterExplicador(Map<String, String> searchParams) {
        FilterExplicadorObject filterExplicadorObject = new FilterExplicadorObject(searchParams);
        Set<Explicador> explicadores = this.findAll();

        return this.filterService.filter(explicadores, filterExplicadorObject);
    }
}
