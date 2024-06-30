# Snowblind

## Overview

**Snowblind** is a laser show software written in Kotlin, using JavaFX and LWJGL. It's designed to integrate with digital audio workstations (DAWs), providing tools for creating laser shows.

## Features

### DAW Integration

- Acts as a clip launcher, timeline, and MIDI input using a DAW.
- Connects to the DAW using a CLAP or VST3 plugin: check out [snowblind_ingress](https://github.com/defvs/snowblind_ingress).
- Supports up to 32 macro inputs from the DAW to the node-based editor.
- Utilizes existing DAW tools such as automation, piano roll, MIDI clips, loops, and live creation tools for synchronizing live production with lasers.

### Visual Capabilities

- Offers both 2D and 3D visualization.

### Node-Based Editor

- Node-based editor for creating and editing clips and effects.

### Multi-Zone Output

- Allows output to multiple lasers across multiple zones.

### Customization and Extensibility

- Aimed at static live show creators but can be extended based on the DAW used.

### Cross-Platform Compatibility

- Runs on any platform that supports JVM.
- Works with multiple DAWs through CLAP and VST3.

## Trivia

### Name Origin

The software is named after the track "Snowblind" by Au5.

### Inspiration

Developed out of a need for better macro and timeline features not found in other software like Pangolin Quickshow, LaserShowGen, and Liberation. The node-based editing concept is inspired by Liberation, which was a standout feature.

## Building and Running

### Requirements

- **Build Tool**: Gradle
- **JDK**: Version 21

### Commands

- **Running the Project**:
  ```sh
  gradlew run
  ```

- **Building the JAR**:
  ```sh
  gradlew shadowJar
  ```

## Contributing

Contributions are welcome. Feel free to submit a pull request or open an issue on GitHub.

## License

This project is licensed under GPLv3 - see [LICENSE](LICENSE) file for details.
