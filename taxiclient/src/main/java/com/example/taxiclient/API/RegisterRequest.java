package com.example.taxiclient.API;

public class RegisterRequest {



        private  String login;
        private String password;

    public RegisterRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }


    }


