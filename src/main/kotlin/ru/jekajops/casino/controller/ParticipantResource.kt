package ru.jekajops.casino.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import ru.jekajops.casino.Participant
import ru.jekajops.casino.ParticipantRepository
import java.io.IOException
import java.util.*

@RestController
@RequestMapping("/participants")
class ParticipantResource(
    private val participantRepository: ParticipantRepository,
    private val objectMapper: ObjectMapper,
) {
//    @GetMapping
//    suspend fun getList(pageable: Pageable): Flow<Participant> {
//        return participantRepository.findAll(pageable)
//    }

    @GetMapping("/{id}")
    suspend fun getOne(@PathVariable id: Long): Participant {
        val participantOptional: Participant? = participantRepository.findById(id)
        return participantOptional ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Entity with id `$id` not found"
        )
    }

    @GetMapping("/by-ids")
    fun getMany(@RequestParam ids: List<Long>): Flow<Participant> {
        return participantRepository.findAllById(ids)
    }

    @PostMapping
    suspend fun create(@RequestBody participant: Participant): Participant {
        return participantRepository.save(participant)
    }

    @PatchMapping("/{id}")
    @Throws(IOException::class)
    suspend fun patch(@PathVariable id: Long, @RequestBody patchNode: JsonNode): Participant {
        val participant: Participant = participantRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `$id` not found")
        objectMapper.readerForUpdating(participant).readValue<Participant>(patchNode)
        return participantRepository.save(participant)
    }

    @PatchMapping
    @Throws(IOException::class)
    suspend fun patchMany(@RequestParam ids: List<Long>, @RequestBody patchNode: JsonNode): Flow<Long> {
        val participants: Flow<Participant> = participantRepository.findAllById(ids)
        participants.collect {
            objectMapper.readerForUpdating(it).readValue<Participant>(patchNode)
        }
        val resultParticipants: Flow<Participant> = participantRepository.saveAll(participants)
        return resultParticipants.map { it.id!! }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long): Participant? {
        val participant: Participant? = participantRepository.findById(id)
        participant?.let { participantRepository.delete(it) }
        return participant
    }

    @DeleteMapping
    suspend fun deleteMany(@RequestParam ids: List<Long>) {
        participantRepository.deleteAllById(ids)
    }
}
