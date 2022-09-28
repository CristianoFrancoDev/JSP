package it.contrader.service;

import it.contrader.dto.UtenteDTO;
import it.contrader.model.Utente;

//singleton
public class RegisterService extends AbstractService<Utente, UtenteDTO>
{
    private static RegisterService instance;

    private RegisterService()
    {}

    public static RegisterService getInstance()
    {
        if (instance == null)
            instance = new RegisterService();

        return instance;
    }


}
