package nl.taskmate.boardservice.data;

import nl.taskmate.boardservice.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    @Query("SELECT e FROM Board e where :username in (e.users) or :username = e.owner")
    Set<Board> findBoardsByAssignedUsers(@Param("username") String username);

    Set<Board> findBoardsByOwnerIgnoreCase(String username);
}
