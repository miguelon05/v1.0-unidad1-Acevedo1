package pe.upeu.andinasalud.presentation.perfil

import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.presentation.components.CargaViewModel

class PerfilViewModel(obtener: ObtenerPacienteUseCase) : CargaViewModel<Paciente>({ obtener() })
