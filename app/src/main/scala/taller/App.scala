package taller

object App {
  def main(args: Array[String]): Unit = {
    val r = new RegistroSismico()
    //println(r.conteoPorRegion(Datos.registro))
    println(r.fuertes(Datos.registro, 5.0))
    println(r.enRegion(Datos.registro, "Valle"))
    println(r.paresCercanos(Datos.registro, 0.6))
    //Punto 2
    println(r.porRegion(Datos.registro))
    println(r.conteoPorRegion(Datos.registro))
    println(r.magnitudMaxima(Datos.registro))
    //Punto 3
    println(r.conteoSeguro(Datos.registro))
    println(r.maximaDe(Datos.registro,"Amazonas"))
    println(r.regionMasActiva(Datos.registro))
   println(r.informe(Datos.registro, 3.0))
  }

}
