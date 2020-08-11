package othello.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import othello.model.BoardModel;

public interface BoardRepository extends CrudRepository<BoardModel, Integer> {
    @Query(value = "SELECT * FROM board ORDER BY id DESC LIMIT 1", nativeQuery = true)
    public BoardModel findLatest();
}