(ns cloturas.routes.home
  (:use compojure.core)
  (:require [cloturas.views.layout :as layout]
            [cloturas.util :as util]
            [cloturas.models.db :as db]
            [noir.session :as session]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn lista-paciente-page [& error]
  (layout/render
    "pacientes.html" {:error   error
                 :pacientes (db/get-pacientes)}))

(defn crea-paciente-page [paciente]
  (println (str "paciente vale: " paciente))
  (cond
   (empty? (:nombre paciente))
   (lista-paciente-page "Un paciente debe tener un nombre.")
   (empty? (:nif paciente))
   (lista-paciente-page "Un paciente debe tener un NIF/NIE.")
   (empty? (:precio_consulta paciente))
   (lista-paciente-page "Un paciente debe tener un precio base para poder facilitarte el trabajo.")
   :else
   (do
     (db/crea-paciente paciente)
     (lista-paciente-page)
     )
   ))

(defn nueva-factura 
  []
  (layout/render "nueva_factura.html" {:pacientes (db/get-pacientes)}))

(defn check-logged-in
  [function]
  (if (session/get :user)
    (function)
    (home-page)))

(defn crea-facturas
  [parameters]
  (println "Creando facturas con:")
  (println parameters)
  (nueva-factura))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/p" [] (check-logged-in lista-paciente-page))
  (POST "/p" {params :form-params} (crea-paciente-page params))
  (GET "/f" [] (nueva-factura))
  (POST "/f" {params :form-params} (crea-facturas params))
  (GET "/about" [] (about-page)))
