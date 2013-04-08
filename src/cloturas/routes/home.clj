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
  (layout/render "nueva_factura.html" {:pacientes (db/get-pacientes) :default_month (util/default-month)}))

(defn go-home-unregistered []
  (session/flash-put! :error "Not logged in!")
  (home-page))

(defn check-logged-in
  [function]
  (if (session/get :user)
    (function)
    (go-home-unregistered)))

(defn graba-factura
  [mes anyo paciente_id num_sesiones importe_sesion]
  (println (str "nueva factura m:" mes " a:" anyo " p:" paciente_id " s:" num_sesiones " :i" importe_sesion)))

(defn crea-facturas
  [{anyo-m "anyo" pacientes "paciente_id" sesiones "num_sesiones" importes "importe_sesion"}]
  ;;[params]
  (println "Creando facturas con:")
  ;;(println "(map (fn [p, s, i] (graba-factura 1 1999 p s i)) " (str pacientes " " sesiones " " importes))
  (map (fn [p, s, i] (graba-factura 1 1999 p s i)) pacientes sesiones importes)
  (nueva-factura))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/p" [] (check-logged-in lista-paciente-page))
  (POST "/p" {params :form-params} (crea-paciente-page params))
  (GET "/f" [] (nueva-factura))
  (POST "/f" {params :form-params} (crea-facturas params))
  (GET "/about" [] (about-page)))
