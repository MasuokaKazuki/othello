package othello.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
@Table(name = "board")
public class BoardModel {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;
  private String pieces;
  private String player;
  
  @Column(name = "created_datetime")
  private Date createdDatetime;
  
}