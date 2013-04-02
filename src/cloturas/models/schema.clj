(ns cloturas.models.schema
  (:require [clojure.java.jdbc :as sql]
            [noir.io :as io]))

(def db-store "cloturas.db")

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname (str (io/resource-path) db-store)
              :user "sa"
              :password ""
              :naming {:keys clojure.string/upper-case
                       :fields clojure.string/upper-case}})
(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new java.io.File (str (io/resource-path) db-store ".h2.db"))))

(defn create-users-table
  []
  (sql/with-connection db-spec
    (sql/create-table
      :users
      [:id "varchar(20) PRIMARY KEY"]
      [:first_name "varchar(30)"]
      [:last_name "varchar(30)"]
      [:email "varchar(30)"]
      [:admin :boolean]
      [:last_login :time]
      [:is_active :boolean]
      [:pass "varchar(100)"])))

(defn crea-tabla-pacientes
  []
  (sql/with-connection db-spec
    (sql/create-table
      :pacientes
      [:id "integer PRIMARY KEY AUTO_INCREMENT"]
      [:nombre "varchar(60)"]
      [:nif "varchar(16)"]
      [:email "varchar(30)"]
      [:precio_consulta "decimal"]
      [:activo :boolean]
      ["UNIQUE" "(nombre, nif)"])))

(defn crea-tabla-facturas
  []
  (sql/with-connection db-spec
    (sql/create-table
      :facturas
      [:id "INTEGER PRIMARY KEY AUTO_INCREMENT"]
      [:fecha "DATE"]
      [:paciente_id "integer"]
      [:num_sesiones "integer"]
      [:importe_sesion "decimal"])
    (sql/do-commands 
     "CREATE INDEX fecha_index ON facturas(fecha)")))

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-users-table) (crea-tabla-pacientes) (crea-tabla-facturas)
)
