package taller

/**
 * Taller 3 — consultas sobre un registro sísmico con expresiones for, mapas y
 * secuencias perezosas.
 *
 * Complete cada función. Las firmas y los tipos no se modifican.
 */
class RegistroSismico {

  type Registro = List[Sismo]

  // Punto 1: consultas con expresiones for --------------------------------------

  /** Las regiones de los sismos que superan el umbral, en orden y con repetidos. */
  def fuertes(r: Registro, umbral: Double): List[String] = {

    val regionesFuertes =
      for{
        p <- r
        if(p.magnitud > umbral)
      }yield (p.region)
    regionesFuertes// Devuelve una lista con las regiones que tuvieron un sismo mas fuerte que el umbral establecido
  }

  /** Las magnitudes registradas en una región. */
  def enRegion(r: Registro, region: String): List[Double] = {
    val MagnitudesRegion=
      for{
        q <- r
        if(q.region == region)
      }yield(q.magnitud)
    MagnitudesRegion
  }

  /** Parejas de regiones distintas con algún sismo de magnitudes cercanas. */
  def paresCercanos(r: Registro, dif: Double): List[(String, String)] = {
    val regiones = r.map(_.region).distinct //el distinct sirve para que hayan regiones unicas que estan en orden de primera aparicion
    val parCercano =
      for{
        (q1,i) <- regiones.zipWithIndex//Para poder extraer el indice
        (q2,e) <- regiones.zipWithIndex
        if(i>e) //Se utiliza exist para que solo aparezca una vez las parejas que cumplen la condicion
        if r.exists(s1 =>
          s1.region == q1 &&
          r.exists(s2 =>
         s2.region == q2 && math.abs(s1.magnitud - s2.magnitud) <= dif))//Math.abs es para poder realizar la operacion con valor absoluto en este caso |magnitudA - MagnitudB| <= dif
      }yield (q2,q1)
    parCercano
  }

  // Punto 2: agrupar con mapas --------------------------------------------------

  def porRegion(r: Registro): Map[String, List[Sismo]] ={
    r.groupBy(_.region)//Agrupa cada region quedando como la key y cada una contiene sus valores las cuales son las listas con la respetiva informacion de esa region
  }

  def conteoPorRegion(r: Registro): Map[String, Int] = {

   val countRegion= porRegion(r).map{
     case (region,sismo)=>
        region->sismo.size //Cuenta cuantos sismos ocurrio en cada region
    }
    countRegion//Devuelve la region con la cantidad de sismos que ocurrieron en ella
  }

  def magnitudMaxima(r: Registro): Map[String, Double] = {
    val MaxMagnitudRegion= porRegion(r).map{
      case (region,sismo)=>
        region->sismo.map(_.magnitud).max//Con esto hallo la magnitud maxima del sismo que hay dentro de la lista de magnitudes asociadas a la region key
    }
    MaxMagnitudRegion
  }

  // Punto 3: consultas seguras --------------------------------------------------

  /** Un mapa que responde cero por las regiones que no están en el registro. */
  def conteoSeguro(r: Registro): Map[String, Int] = {
    conteoPorRegion(r).withDefaultValue(0)//se pone 0 por default en caso de que se ponga una region que no este en el registro de lo contrario se pone el valor de la key si solo si esta en el registro
  }

  /** La magnitud máxima de una región, o vacío si no hay registros suyos. */
  def maximaDe(r: Registro, region: String): Option[Double] = {
    magnitudMaxima(r).get(region)//Llamo la funcion y como ella devuelve un map con el metodo .get le meto el valor dado por parametros para que me devuelva su valor pero ya de forma segura en caso de una excepcion
  }

  /** La región con más sismos; en empate, la primera en orden alfabético. */
  def regionMasActiva(r: Registro): Option[String] = {
    val conteoByregion = conteoPorRegion(r)
    if (conteoByregion.isEmpty)
      None
    else
      Some(conteoByregion.minBy {
        case (region, cantidad) =>
          (-cantidad, region)//Se pone cantidad negativa ya que la idea es que gane la que menos tiene. en este caso la que menos tiene seria la que mas
                            //tiene ya que ejemplo: -3 < -2 en esta gana la region con el -3 que en realidad seria la mayor
      }._1) //Devuelve nombre de la region con mas actividad sismica
  }

  // Punto 4: réplicas perezosas -------------------------------------------------

  def redondear(x: Double): Double = Math.round(x * 100.0) / 100.0

  /** La secuencia infinita que empieza en la magnitud y multiplica por 0.8. */
  def replicas(magnitud: Double): LazyList[Double] = {
    def redondear(x: Double): Double = Math.round(x * 100.0) / 100.0 //Redondea a 2 decimales
    def paso(m: Double): LazyList[Double] =
      redondear(m) #:: paso(redondear(0.8 * m)) //Aqui se toma el primer elemento y se añade en la lista y la parte de la derecha como es evaluacion peresosa se queda congelada hasta que se le pida calcular halgo
    paso(magnitud)
  }

  /** Las réplicas mientras superen el mínimo, incluida la primera. */
  def replicasSensibles(magnitud: Double, minimo: Double): List[Double] = {
    replicas(magnitud).takeWhile(_ > minimo).toList //Solo evalua los numeros de la lazyList hasta encontrar uno que falle es decir un numero que no sea mayor al minimo
  }

  // Punto 5: informe combinado --------------------------------------------------

  /** Conteo, magnitud máxima y réplicas esperadas del sismo más fuerte, por región. */
  def informe(r: Registro, minimo: Double): Map[String, Resumen] = {
    val magnitudesMaximas = magnitudMaxima(r)
    val informeSismos =
      (for{  //Se itera sobre las key y valores de conteoPorRegion
        (region, cantidad)<- conteoPorRegion(r)
        //Se busca la magnitud maxima por cada region key que se va iterando
         maximaMagnitud= magnitudesMaximas(region)
         cantidadRepli = replicasSensibles(maximaMagnitud,minimo).size //Se calculan las replicas utilizando maximaMagnitud la cual es double
      }yield
        region->Resumen(cantidad, maximaMagnitud,cantidadRepli )//queda key con su respectivo resumen de los totales de datos que hay en cada region
      ).toMap //Se convierte a map ya que la expresion for devuelve un iterable

    informeSismos
  }
}
