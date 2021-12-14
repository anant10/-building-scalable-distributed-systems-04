package servlet;

import com.google.gson.Gson;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.ResortLiftRideDao;
import model.NumSkiers;
import model.Resort;
import model.ResortsList;
import model.ResponseMessage;
import model.SeasonsList;

@WebServlet(name = "servlet.ResortsServlet", urlPatterns = "/resorts/*")
public class ResortsServlet extends HttpServlet {
  private Gson gson  = new Gson();
  private static final int DAY_ID_MIN = 1;
  private static final int DAY_ID_MAX = 366;
  private static final String SEASONS_PARAMETER = "seasons";
  private static final String DAYS_PARAMETER = "days";
  private static final String SKIERS_PARAMETER = "skiers";
  private static final ResortLiftRideDao resortLiftRideDao = new ResortLiftRideDao();

  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");

    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
          IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
//    System.out.println(urlPath);
    // check we have a URL!
    if (urlPath == null ) {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      Resort resort = new Resort("dummy", 1);
      ResortsList resortsList = new ResortsList();
      resortsList.add(resort);
      res.getWriter().write(gson.toJson(resortsList));
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      try{
        NumSkiers numSkiers = new NumSkiers(getUniqueNumberOfSkierIdsFromResortDB(urlParts));
        res.getWriter().write(gson.toJson(numSkiers));
      }
      catch (IllegalArgumentException e){
        ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
        res.getWriter().write(gson.toJson(responseMessage));
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      }
    }
  }

  private int getUniqueNumberOfSkierIdsFromResortDB(String[] urlParts){
    try{
      int resortId = Integer.parseInt(urlParts[1]);
      int season = Integer.parseInt(urlParts[3]);
      int day = Integer.parseInt(urlParts[5]);
      int count = resortLiftRideDao.getUniqueSkiers(resortId, season, day);
      return count;
    }
    catch (Exception e){
      throw new IllegalArgumentException();
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skiers"
    // urlParts = [, 1, seasons, 2019, day, 1, skiers]
    if (urlPath.length == 7) {
      try {
        for (int i = 1; i < urlPath.length; i += 2) {
          Integer.parseInt(urlPath[i]);
        }
        return (urlPath[3].length() == 4
                && Integer.valueOf(urlPath[5]) >= DAY_ID_MIN
                && Integer.valueOf(urlPath[5]) <= DAY_ID_MAX
                && urlPath[2].equals(SEASONS_PARAMETER)
                && urlPath[4].equals(DAYS_PARAMETER)
                && urlPath[6].equals(SKIERS_PARAMETER));
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }
//  private boolean isUrlValid(String[] urlPath) {
//    // TODO: validate the request url path according to the API spec
//    // urlPath  = "/1/seasons/2019/day/1/skier/123"
//    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
//     if (urlPath.length == 3) {
//      try {
//        Integer.parseInt(urlPath[1]);
//        return (urlPath[2].equals("seasons"));
//      } catch (Exception e) {
//        return false;
//      }
//    }
//     else return urlPath.length == 1;
//  }
}
