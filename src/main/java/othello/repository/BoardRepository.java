package othello.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import othello.model.BoardModel;

@Service
public interface BoardRepository extends CrudRepository<BoardModel, Integer> {

}