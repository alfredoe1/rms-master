package com.mitrais.rms.controller;

import com.mitrais.rms.dao.UserDao;
import com.mitrais.rms.dao.impl.UserDaoImpl;
import com.mitrais.rms.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends AbstractController
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String path = getTemplatePath(req.getServletPath()+req.getPathInfo());
        UserDao userDao = UserDaoImpl.getInstance();

        if ("/list".equalsIgnoreCase(req.getPathInfo())){            
            List<User> users = userDao.findAll();
            req.setAttribute("users", users);
        }
        
        if ("/edit".equalsIgnoreCase(req.getPathInfo())) {
			Long userId = Long.parseLong(req.getParameter("userId"));
			User user = userDao.find(userId).get();
			req.setAttribute("user", user);
		}
        
        if ("/delete".equalsIgnoreCase(req.getPathInfo())) {
			Long userId = Long.parseLong(req.getParameter("userId"));
			User user = userDao.find(userId).get();
			boolean isSuccess = userDao.delete(user);
			
			//reload list page
			List<User> users = userDao.findAll();
			req.setAttribute("users", users);
			req.setAttribute("isDeleteSuccess", isSuccess);
			resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/list");
			return;
		}
        
        if ("/home".equalsIgnoreCase(req.getPathInfo()))
        {
			resp.sendRedirect(req.getContextPath() + "/index.jsp");
			return;
		}

        RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
        requestDispatcher.forward(req, resp);
    }
    
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		UserDao userDao = UserDaoImpl.getInstance();
		User user;
		
		String username = req.getParameter("username");
		String password = req.getParameter("userpass");
		String userid = req.getParameter("userId");
		
		//System.out.println( userid + " " +username +" "+ password);
		
		//add process
		if (userid == null || userid.isEmpty()) 
		{			
			user = new User(null, username, password);
			userDao.save(user);
		} 
		
		//update process
		if (!(userid == null || userid.isEmpty()))
		{
			user = new User(Long.parseLong(userid), username, password);
			userDao.update(user);
		}
		
		//reload list page 
		List<User> users = userDao.findAll();
		req.setAttribute("users", users);		
		resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/list");
	}

}
