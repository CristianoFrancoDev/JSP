package it.contrader.dao;

import java.util.List;

public interface DAO<Entity>
{

	public List<Entity> getAll();
	
	public Entity read(int id);
	
	public boolean insert(Entity dto);
	
	public boolean update(Entity dto);
	
	public boolean delete(int id);
}
