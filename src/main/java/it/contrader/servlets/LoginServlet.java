package it.contrader.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.contrader.dto.UtenteDTO;
import it.contrader.service.UtentiService;

/*
 * Login Servlet
 */
public class LoginServlet extends HttpServlet {
	// UID della servlet
	private static final long serialVersionUID = 1L;

	/**
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * 
	 * Metodo che gestisce le request che arrivano dalla JSP.
	 */
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final HttpSession session = request.getSession();

		if (request != null)
		{
			if (request.getParameter("pulsante") != null)
			{
				//azzeramento attributo session
				session.setAttribute("utente", null);

				switch (request.getParameter("pulsante"))
				{
					case "Login":
						UtentiService service = UtentiService.getInstance();

						//ottenimento parametri
						String username = request.getParameter("username");
						String password = request.getParameter("password");

						//come nei vecchi controller, invoca il service
						UtenteDTO dto = service.login(username, password);

						if (dto != null)
							//se il login ha funzionato, salva l'utente nella sessione
							session.setAttribute("utente", dto);
						else
							//altrimenti torna alla pagina di login
							getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

						//esegue una switch case in base allo usertype per il reindirizzamento
						switch (dto.getRuolo().name().toUpperCase())
						{
							case "ADMIN":
								//questo metodo reindirizza alla JSP tramite URL con una request e una response
								getServletContext().getRequestDispatcher("/homeadmin.jsp").forward(request, response);
								break;

							case "USER":
								getServletContext().getRequestDispatcher("/homeUser.jsp").forward(request, response);
								break;

							case "AMMINISTRATORE":
								getServletContext().getRequestDispatcher("/homeAmministratore.jsp").forward(request, response);
								break;

							default:
								//di default rimanda al login
								getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
								break;
						}

						break;
					case "Register":
						//Pagina di registrazione
						this.getServletContext().getRequestDispatcher("/Register.jsp").forward(request, response);

						break;
					default:
						//di default rimanda al login
						getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

						break;
				}
			}
		}
	}
}
