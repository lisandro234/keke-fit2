package com.pec.kekefit.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pec.kekefit.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private val fullHistory = mutableListOf<MessagePayload>()

    private val systemPrompt = """
        Sos KekeBot, el asistente inteligente de la app Keke-Fit.

        Respondés siempre en español, con un tono amigable, claro y motivador.

        Tu función principal es ayudar con:
        - Alimentación saludable
        - Ideas de comidas
        - Calorías y macronutrientes
        - Hábitos fitness
        - Motivación
        - Organización de rutinas simples
        - Dudas generales sobre la app Keke-Fit
        - Informe mensual de comidas
        - Racha, progreso y seguimiento de hábitos
        - Preferencias de ingredientes y restricciones
        - Horarios recomendados de comida

        Reglas:
        - No reemplazás a un médico ni nutricionista.
        - La app es de uso suplementario; ante consultas médicas o nutricionales importantes, recomendás consultar a un profesional.
        - Si el usuario menciona enfermedades, medicación, síntomas o trastornos alimenticios, recomendás consultar a un profesional.
        - Respondés de forma práctica, clara y no demasiado larga.
        - Evitás dar diagnósticos médicos.
        - Usás emojis de vez en cuando, sin exagerar.
        - Brochero Juan, Luca Galano, Nacho Alonso, Torrilla Lisandro y Smich Máximo hicieron la app Keke Fit.
    """.trimIndent()

    init {
        fullHistory.add(MessagePayload("system", systemPrompt))
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _uiState.value.isLoading) return

        if (BuildConfig.GROQ_API_KEY.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Falta configurar la API Key de Groq")
            return
        }

        val userMessage = ChatMessage(role = "user", content = userText)

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            error = null
        )

        fullHistory.add(MessagePayload("user", userText))

        viewModelScope.launch {
            try {
                val contextMessages = buildList {
                    add(fullHistory.first())
                    addAll(fullHistory.drop(1).takeLast(14))
                }

                val response = GroqClient.service.sendMessage(
                    auth = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = GroqRequest(messages = contextMessages)
                )

                if (response.isSuccessful) {
                    val reply = response.body()
                        ?.choices
                        ?.firstOrNull()
                        ?.message
                        ?.content
                        ?: "No pude generar una respuesta."

                    val botMessage = ChatMessage(role = "assistant", content = reply)
                    fullHistory.add(MessagePayload("assistant", reply))

                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + botMessage,
                        isLoading = false
                    )
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "API Key inválida. Revisá Groq."
                        429 -> "Demasiadas solicitudes. Esperá un momento."
                        500 -> "Error del servidor de Groq."
                        else -> "Error ${response.code()}: ${response.message()}"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No se pudo conectar. Revisá internet."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearChat() {
        fullHistory.clear()
        fullHistory.add(MessagePayload("system", systemPrompt))
        _uiState.value = ChatUiState()
    }
}
