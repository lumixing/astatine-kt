# astatine
## ⚠ rewriting in rust with bevy https://github.com/lumixing/astatine
<p align="center">
  <img src="https://img.shields.io/badge/version-undefined-critical?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/made%20with-kotlin-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/powered%20by-libktx-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/made%20by-lumix-dodgerblue?style=for-the-badge"/>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/highly-radioactive-yellow?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/not%20a%20bug-a%20feature-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/chemically-accurate-important?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/really-optimized-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/borrows-some%20code-pink?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/better%20than-terraria-blueviolet?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/crypto-miner-yellow?style=for-the-badge"/>
</p>
<p align="center">
  basically a terraria clone, <i>but better!</i>
</p>

## screenshots
<p align="center">
  <img src="https://kappa.lol/4Zgx3"/>
</p>

## features
* world stored in chunks
* procedural world terrain generation
* uses entity-component-system
* customs physics with jbump's aabb collisions
* basic player movement
* *very* basic inventory system
* debug features
  * debug overlay text (top left and right)
  * `F1` to toggle entity collision boxes
  * `F2` to toggle chunk borders
  * `F3` to clear inventory
  * `+` to zoom in camera
  * `-` to zoom out camera
  * `0` to reset camera zoom

## libraries used
* [libktx](https://libktx.github.io/): game engine/framework based on [libgdx](https://libgdx.com/)
* [jbump](https://github.com/implicit-invocation/jbump): collision detection for physics
* [joise](https://joise.sudoplaygames.com/): noise library for terrain generation

## build with gradle
run `gradlew build` inside the folder

## similar projects to check out
* https://github.com/egordorichev/LastTry (java)
* https://github.com/jmrapp1/TerraLegion (java)
* https://github.com/hexabeast/HexBox (java)
* https://github.com/ktualhu/Minecube-Terraria-clone (java)
* https://github.com/NaulaN/Terraria-javafx (java)
* https://github.com/nazarlvr/Game-project (java)
* https://github.com/sreich/ore-infinium (kotlin)
* https://github.com/FergusGriggs/Fegaria-Remastered (python)

## useful resources
* https://accidentalnoise.sourceforge.net/minecraftworlds.html for terrain generation (base of joise)
