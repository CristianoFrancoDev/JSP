package it.contrader.dao;

import it.contrader.converter.BigliettoConverter;
import it.contrader.converter.PistaConverter;
import it.contrader.converter.UtenteConverter;
import it.contrader.model.Biglietto;
import it.contrader.model.Pista;
import it.contrader.model.Utente;
import it.contrader.service.BigliettiService;
import it.contrader.service.PisteService;
import it.contrader.service.UtentiService;
import it.contrader.utils.LinkDB;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//singleton
public class Dao_Biglietti implements DAO<Biglietto>
{
    private final String QUERY_ALL = "SELECT * FROM biglietti";
    private final String QUERY_CREATE = "INSERT INTO biglietti (id_utente, id_pista, data) VALUES (?, ?, ?)";
    private final String QUERY_READ = "SELECT * FROM biglietti WHERE id = ?";
    private final String QUERY_READ_LAST_TICKET_INSERTED = "SELECT LAST_INSERT_ID()";
    private final String QUERY_READ_BY_ID_USER = "SELECT * FROM biglietti WHERE id_utente = ?";
    private final String QUERY_UPDATE = "UPDATE biglietti SET id_utente = ?, id_pista = ?, data = ? WHERE id = ?";
    private final String QUERY_DELETE = "DELETE FROM biglietti WHERE id = ?";
    private final String QUERY_READ_BIGLIETTI_BY_IMPIANTO = "SELECT biglietti.id FROM impianti " +
            "JOIN piste ON impianti.id = piste.id_impianto JOIN biglietti ON biglietti.id_pista = piste.id " +
            "JOIN utenti ON utenti.id = biglietti.id_utente WHERE impianti.titolo = ? AND utenti.nome = ?";
    private final String QUERY_FILTER_BY_DATE = "SELECT * FROM biglietti WHERE id_utente = ? AND DATE(data) BETWEEN ? AND ?";
    private final String QUERY_FILTER_BY_RACETRACK = "SELECT * FROM biglietti WHERE id_pista = ? AND id_utente = ?";
    private static Connection connection;
    private static Dao_Biglietti instance;

    private Dao_Biglietti()
    {

    }

    public static Dao_Biglietti getInstance()
    {
        if (instance == null)
            instance = new Dao_Biglietti();

        return instance;
    }

    public Biglietto read(int id)
    {
        UtentiService utentiService = UtentiService.getInstance();
        UtenteConverter utenteConverter = UtenteConverter.getInstance();
        PisteService pisteService = PisteService.getInstance();
        PistaConverter pistaConverter = PistaConverter.getInstance();
        Biglietto response = null;

        connection = LinkDB.getConnection();

        if (connection != null)
        {
            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_READ);
                preparedStatement.setInt(1, id);
                preparedStatement.execute();

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next())
                {
                    Utente utente = utenteConverter.toEntity(utentiService.read(resultSet.getInt(2)));
                    Pista pista = pistaConverter.toEntity(pisteService.read(resultSet.getInt(3)));

                    response = new Biglietto(resultSet.getInt(1),
                            utente,
                            pista,
                            resultSet.getDate(4).toLocalDate());
                }

                resultSet.close();
                preparedStatement.close();
                LinkDB.closeConnection();
            }
            catch (Exception ex)
            {
                response = null;
                ex.printStackTrace();
            }
        }

        return response;
    }

    @Override
    public boolean delete(int id)
    {
        boolean response = true;

        connection = LinkDB.getConnection();

        if (connection == null)
            response = false;
        else
        {
            try
            {
                PreparedStatement statement = connection.prepareStatement(QUERY_DELETE);
                statement.setInt(1, id);
                statement.execute();
                statement.close();
                LinkDB.closeConnection();
            }
            catch (SQLException e)
            {
                response = false;
                e.printStackTrace();
            }
        }

        return response;
    }

    @Override
    public List<Biglietto> getAll()
    {
        UtentiService utentiService = UtentiService.getInstance();
        UtenteConverter utenteConverter = UtenteConverter.getInstance();
        PisteService pisteService = PisteService.getInstance();
        PistaConverter pistaConverter = PistaConverter.getInstance();
        Biglietto biglietto;
        Utente utente;
        Pista pista;
        List<Biglietto> response = null;

        connection = LinkDB.getConnection();

        if (connection != null)
        {
            response = new ArrayList<>();

            try
            {
                Statement statement = connection.createStatement();
                statement.execute(QUERY_ALL);

                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next())
                {
                    utente = utenteConverter.toEntity(utentiService.read(resultSet.getInt(2)));
                    pista = pistaConverter.toEntity(pisteService.read(resultSet.getInt(3)));

                    response.add(new Biglietto(resultSet.getInt(1),
                            utente,
                            pista,
                            resultSet.getDate(4).toLocalDate()));
                }

                resultSet.close();
                statement.close();
                LinkDB.closeConnection();
            }
            catch (SQLException e)
            {
                response = null;
                e.printStackTrace();
            }
        }

        return response;
    }

    @Override
    public boolean insert(Biglietto biglietto)
    {
        PreparedStatement statement;
        boolean response = false;

        connection = LinkDB.getConnection();

        if (connection != null)
        {
            response = true;

            try
            {
                //creazione biglietto
                statement = connection.prepareStatement(QUERY_CREATE);
                statement.setInt(1, biglietto.getUtente().getId());
                statement.setInt(2, biglietto.getPista().getId());
                statement.setDate(3, Date.valueOf(biglietto.getData()));

                statement.executeUpdate();

                Statement stmt = connection.createStatement();
                stmt.execute(QUERY_READ_LAST_TICKET_INSERTED);

                ResultSet resultSet = stmt.getResultSet();

                if (resultSet.next())
                    biglietto.setId(resultSet.getInt(1));
                else
                    response = false;

                resultSet.close();
                stmt.close();

                statement.close();
                LinkDB.closeConnection();
            }
            catch (Exception ex)
            {
                response = false;
                ex.printStackTrace();
            }
        }

        return response;
    }

    @Override
    public boolean update(Biglietto biglietto)
    {
        PreparedStatement statement;
        boolean response = false;

        connection = LinkDB.getConnection();

        if (connection != null)
        {
            response = true;

            try
            {
                    //modifica biglietto
                    statement = connection.prepareStatement(QUERY_UPDATE);
                    statement.setInt(1, biglietto.getUtente().getId());
                    statement.setInt(2, biglietto.getPista().getId());
                    statement.setDate(3, Date.valueOf(biglietto.getData()));
                    statement.setInt(4, biglietto.getId());

                    statement.executeUpdate();

                statement.close();
                LinkDB.closeConnection();
            }
            catch (Exception ex)
            {
                response = false;
                ex.printStackTrace();
            }
        }

        return response;
    }

    public List<Biglietto> findByUser(Utente utente)
    {
        UtentiService utentiService = UtentiService.getInstance();
        UtenteConverter utenteConverter = UtenteConverter.getInstance();
        PisteService pisteService = PisteService.getInstance();
        PistaConverter pistaConverter = PistaConverter.getInstance();
        Biglietto biglietto;
        Utente utente2;
        Pista pista;
        List<Biglietto> response = null;

        connection =  LinkDB.getConnection();

        if (connection != null)
        {
            response = new ArrayList<>();

            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_READ_BY_ID_USER);
                preparedStatement.setInt(1, utente.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next())
                {
                    utente2 = utenteConverter.toEntity(utentiService.read(resultSet.getInt(2)));
                    pista = pistaConverter.toEntity(pisteService.read(resultSet.getInt(3)));

                    biglietto = new Biglietto(resultSet.getInt(1),
                            utente2,
                            pista,
                            resultSet.getDate(4).toLocalDate());

                    response.add(biglietto);
                }

                resultSet.close();
                preparedStatement.close();
                LinkDB.closeConnection();
            }
            catch (Exception ex)
            {
                response = null;
                ex.printStackTrace();
            }
        }

        return response;
    }

    public ArrayList<Biglietto> filterBigliettiByData(Utente utente, LocalDate dateFrom, LocalDate dateTo)
    {
        UtentiService utentiService = UtentiService.getInstance();
        UtenteConverter utenteConverter = UtenteConverter.getInstance();
        PisteService pisteService = PisteService.getInstance();
        PistaConverter pistaConverter = PistaConverter.getInstance();
        Biglietto biglietto;
        Pista pista;
        ArrayList<Biglietto> response = null;

        connection =  LinkDB.getConnection();

        if (connection != null)
        {
            response = new ArrayList<>();

            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FILTER_BY_DATE);
                preparedStatement.setInt(1,utente.getId());
                preparedStatement.setDate(2, Date.valueOf(dateFrom));
                preparedStatement.setDate(3, Date.valueOf(dateTo));
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next())
                {
                    pista = pistaConverter.toEntity(pisteService.read(resultSet.getInt("id_pista")));

                    biglietto = new Biglietto(
                            resultSet.getInt("id"),
                            utenteConverter.toEntity(utentiService.findByName(utente.getNome())),
                            pista,
                            resultSet.getDate(4).toLocalDate());

                    response.add(biglietto);
                }

                preparedStatement.close();
                LinkDB.closeConnection();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return response;
    }

    public ArrayList<Biglietto> filterByPista(Pista pista, Utente utente)
    {
        UtentiService utentiService = UtentiService.getInstance();
        UtenteConverter utenteConverter = UtenteConverter.getInstance();
        PisteService pisteService = PisteService.getInstance();
        PistaConverter pistaConverter = PistaConverter.getInstance();
        Biglietto biglietto;
        Utente utente1;
        Pista pista1;
        ArrayList<Biglietto> response = null;

        connection =  LinkDB.getConnection();

        if (connection != null)
        {
            response = new ArrayList<>();

            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FILTER_BY_RACETRACK);
                preparedStatement.setInt(1, pista.getId());
                preparedStatement.setInt(2, utente.getId());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next())
                {
                    utente1 = utenteConverter.toEntity(utentiService.read(resultSet.getInt(2)));
                    pista1 = pistaConverter.toEntity(pisteService.read(resultSet.getInt(3)));

                    biglietto = new Biglietto(resultSet.getInt(1),
                            utente1,
                            pista1,
                            resultSet.getDate(4).toLocalDate());

                    response.add(biglietto);
                }

                resultSet.close();
                preparedStatement.close();
                LinkDB.closeConnection();
            }
            catch (Exception ex)
            {
                response = null;
                ex.printStackTrace();
            }
        }

        return response;
    }

    public List<Biglietto> filterByImpianto(String titolo, Utente utente)
    {
        BigliettiService bigliettiService = BigliettiService.getInstance();
        BigliettoConverter bigliettoConverter = BigliettoConverter.getInstance();
        Biglietto biglietto;
        List<Biglietto> response = null;

        connection = LinkDB.getConnection();

        if (connection != null)
        {
            response = new ArrayList<>();

            try
            {
                PreparedStatement statement = connection.prepareStatement(QUERY_READ_BIGLIETTI_BY_IMPIANTO);
                statement.setString(1, titolo.trim());
                statement.setInt(2, utente.getId());

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next())
                {
                    biglietto = bigliettoConverter.toEntity(bigliettiService.read(resultSet.getInt(1)));
                    response.add(biglietto);
                }

                resultSet.close();
                statement.close();
                LinkDB.closeConnection();
            }
            catch (SQLException e)
            {
                response = null;
                e.printStackTrace();
            }
        }

        return response;
    }
}
