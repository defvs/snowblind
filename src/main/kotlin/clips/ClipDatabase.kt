package clips

import helpers.ClipUUID

object ClipDatabase {
    var database: HashMap<ClipUUID, Clip> = hashMapOf() // TODO: clipDatabase initialization in main program
}