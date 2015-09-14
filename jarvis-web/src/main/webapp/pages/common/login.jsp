
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="cd-user-modal" uname="<% out.print(request.getParameter("uname"));%>"> <!-- this is the entire modal form, including the background -->
  <div class="cd-user-modal-container"> <!-- this is the container wrapper -->

    <div id="cd-login"> <!-- log in form -->
      <form class="cd-form">
        <p class="fieldset">
          <label class="image-replace cd-username" for="signin-uname">Username</label>
          <input class="full-width has-padding has-border" id="signin-uname" type="text" name="uname" placeholder="Username">
          <span class="cd-error-message" id="user-msg">Error message here!</span>
        </p>

        <p class="fieldset">
          <label class="image-replace cd-password" for="signin-password">Password</label>
          <input class="full-width has-padding has-border" id="signin-password" type="password"  placeholder="Password">
          <span class="cd-error-message" id="pass-msg">Error message here!</span>
        </p>

        <p class="fieldset">
          <input class="login-btn" type="submit" value="Login">
        </p>
      </form>

      <!--
      <p class="cd-form-bottom-message"><a href="#0">Forgot your password?</a></p>
      -->
      <!-- <a href="#0" class="cd-close-form">Close</a> -->
    </div> <!-- cd-login -->

    <a href="#0" class="cd-close-form">Close</a>
  </div> <!-- cd-user-modal-container -->
</div> <!-- cd-user-modal -->
