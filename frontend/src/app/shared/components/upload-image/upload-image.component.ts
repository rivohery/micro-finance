import { NgClass } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-upload-image',
  imports: [NgClass],
  templateUrl: './upload-image.component.html',
  styleUrl: './upload-image.component.css',
})
export class UploadImageComponent {
  image = input<string>('');
  onChecked = output<File>();

  imagePreview: string | ArrayBuffer | null = null;
  isDragging = false;
  selectedFile: File | null = null;

  // Gérer la sélection via l'explorateur de fichiers
  onFileSelected(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let file: File | null = element.files ? element.files[0] : null;
    if (file) {
      this.handleFile(file);
    }
  }

  // Gérer le Glisser-Déposer (Drag & Drop)
  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files.length) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  // Lecture du fichier pour la prévisualisation
  private handleFile(file: File) {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(file);
      this.onChecked.emit(file);
    } else {
      alert('Veuillez sélectionner une image valide.');
    }
  }
}
