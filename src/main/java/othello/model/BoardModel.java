package othello.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board")
public class BoardModel {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;
  private String pieces;
  private String player;
  private String playStyle;
  
  @Column(name = "created_datetime")
  private Date createdDatetime;

  @PrePersist
  public void onPrePersist() {
    setCreatedDatetime(new Date());
  }
  
}