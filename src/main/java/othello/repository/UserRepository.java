package othello.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import othello.model.User;

@Service
public interface UserRepository extends CrudRepository<User, Integer> {

}