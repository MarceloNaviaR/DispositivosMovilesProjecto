package edu.ucb.project.feature.tareas

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges

class RepositoryJobs {

    private val coleccion = FirebaseFirestore.getInstance()
        .collection("tareas")

    fun observar(
        onLista: (List<jobs>) -> Unit,
        onNueva: (jobs) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        var cargaInicialCompleta = false
        val documentosConocidos = mutableSetOf<String>()

        return coleccion.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->

            if (error != null) {
                onError(
                    error.localizedMessage
                        ?: "No se pudieron leer las tareas"
                )
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val tareas = snapshot.documents.map { documento ->
                jobs(
                    id = documento.id,
                    titulo = documento.get("titulo") as? String
                        ?: "Sin título",
                    completada = documento.get("completada") as? Boolean
                        ?: false,
                    timestamp = documento.getTimestamp("timestamp")
                        ?.toDate()
                        ?.time
                        ?: 0L
                )
            }

            onLista(
                tareas.sortedByDescending { it.timestamp }
            )

            // Primero esperamos el estado inicial del servidor.
            if (!cargaInicialCompleta) {
                if (!snapshot.metadata.isFromCache) {
                    documentosConocidos.addAll(
                        tareas.map { it.id }
                    )
                    cargaInicialCompleta = true
                }

                return@addSnapshotListener
            }

            val tareasPorId = tareas.associateBy { it.id }

            for (cambio in snapshot.documentChanges) {
                when (cambio.type) {
                    DocumentChange.Type.ADDED -> {
                        val id = cambio.document.id

                        // Evita avisar dos veces por el mismo documento.
                        if (documentosConocidos.add(id)) {
                            tareasPorId[id]?.let(onNueva)
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        documentosConocidos.remove(
                            cambio.document.id
                        )
                    }

                    DocumentChange.Type.MODIFIED -> {
                        // La lista ya se actualizó con onLista.
                        // Una modificación no genera un aviso nuevo.
                    }
                }
            }
        }
    }

    fun agregar(
        titulo: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        val tituloLimpio = titulo.trim()

        if (tituloLimpio.isBlank()) {
            onError("Escribe el título de la tarea")
            return
        }

        val datos = hashMapOf<String, Any>(
            "titulo" to tituloLimpio,
            "completada" to false,
            "timestamp" to FieldValue.serverTimestamp()
        )

        coleccion.add(datos)
            .addOnSuccessListener {
                onExito()
            }
            .addOnFailureListener { error ->
                onError(
                    error.localizedMessage
                        ?: "No se pudo agregar la tarea"
                )
            }
    }
}