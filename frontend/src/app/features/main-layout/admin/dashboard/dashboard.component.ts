import {
  Component,
  OnInit,
  Signal,
  effect,
  inject,
  signal,
  ElementRef,
  ViewChild,
  Inject,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

// Material Modules
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

// ng2-charts Imports
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { StatisticService } from '../../../../core/services/statistic.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule,
    BaseChartDirective,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  statisticService = inject(StatisticService);

  // On récupère l'élément HTML du dashboard
  @ViewChild('dashboard', { static: false }) dashboardElement!: ElementRef;

  // Kpi de la Micro-finance
  totalClients = toSignal(this.statisticService.getNbrTotalOfCustomer(), {
    initialValue: 0,
  });
  totalComptes = toSignal(this.statisticService.getNbrTotalOfAccount(), {
    initialValue: 0,
  });
  totalSoldesGlobal = toSignal(
    this.statisticService.getSoldeTotalOfAccountInMga(),
    { initialValue: 0.0 }
  );

  loadingPieChartData = signal<boolean>(true);
  loadingBarChartData = signal<boolean>(true);
  loadingLineChartData = signal<boolean>(true);

  // ==========================================
  // 1. PIE CHART : Répartition des types de comptes (Volume)
  // ==========================================
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: true, position: 'bottom' },
    },
  };
  public pieChartType: ChartType = 'pie';
  public pieChartData: ChartData<'pie', number[], string | string[]> = {
    labels: [],
    datasets: [],
  };

  // ==========================================
  // 2. BAR CHART : Encours des soldes par type de compte
  // ==========================================
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      x: {},
      y: { min: 0 },
    },
    plugins: {
      legend: { display: false },
    },
  };
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [],
  };

  // ==========================================
  // 3. LINE CHART : Flux d'acquisition des nouveaux clients (Par jour de la semaine)
  // ==========================================
  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      y: { min: 0, ticks: { stepSize: 2 } },
    },
    plugins: {
      legend: { display: true, position: 'top' },
    },
  };
  public lineChartType: ChartType = 'line';
  public lineChartData: ChartData<'line'> = {
    labels: [],
    datasets: [],
  };

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    effect(() => this.handlePieChart());
    effect(() => this.handleBarChart());
    effect(() => this.handleLineChart());
  }

  private handlePieChart(): void {
    const accountStatisticNumberState =
      this.statisticService.accountStatisticNumberState$();
    if (
      accountStatisticNumberState.status === 'OK' &&
      accountStatisticNumberState.value
    ) {
      let labels = [];
      let data = [];
      for (let record of accountStatisticNumberState.value) {
        labels.push(record.accountType);
        data.push(record.nbrAccountByType);
      }
      this.pieChartData.labels = labels;
      this.pieChartData.datasets = [
        {
          data: data,
          backgroundColor: ['#4f46e5', '#10b981', '#f59e0b'],
          hoverBackgroundColor: ['#4338ca', '#059669', '#d97706'],
        },
      ];
      this.loadingPieChartData.set(false);
    }
    if (accountStatisticNumberState.status === 'ERROR') {
      this.loadingPieChartData.set(false);
      console.log(accountStatisticNumberState.error);
    }
  }

  private handleBarChart(): void {
    const accountStatisticSoldState =
      this.statisticService.accountStatisticSoldState$();
    if (
      accountStatisticSoldState.status === 'OK' &&
      accountStatisticSoldState.value
    ) {
      let labels = [];
      let data = [];
      for (let record of accountStatisticSoldState.value) {
        labels.push(record.accountType);
        data.push(record.soldeAccountByType);
      }
      this.barChartData.labels = labels;
      this.barChartData.datasets = [
        {
          data: data,
          label: 'Soldes Totaux',
          backgroundColor: '#6366f1',
          borderRadius: 8,
        },
      ];
      this.loadingBarChartData.set(false);
    }
    if (accountStatisticSoldState.status === 'ERROR') {
      this.loadingBarChartData.set(false);
      console.log(accountStatisticSoldState.error);
    }
  }

  private handleLineChart(): void {
    const registrationStatisticState =
      this.statisticService.registrationStatisticState$();
    if (
      registrationStatisticState.status === 'OK' &&
      registrationStatisticState.value
    ) {
      let labels = [];
      let data = [];
      console.log(registrationStatisticState.value);
      for (let record of registrationStatisticState.value) {
        labels.push(record.dayOfWeek);
        data.push(record.nbrCustomer);
      }
      this.lineChartData.labels = labels;
      this.lineChartData.datasets = [
        {
          data: data,
          label: 'Inscriptions Guichet',
          borderColor: '#4f46e5',
          backgroundColor: 'rgba(79, 70, 229, 0.1)',
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#4f46e5',
        },
      ];
      this.loadingLineChartData.set(false);
    }
    if (registrationStatisticState.status === 'ERROR') {
      this.loadingLineChartData.set(false);
      console.log(registrationStatisticState.error);
    }
  }

  ngOnInit(): void {
    this.statisticService.getAccountStatisticNumber();
    this.statisticService.getAccountStatisticSold();
    this.statisticService.getRegistrationStatisticOfWeek();
  }

  public async exportToPDF(): Promise<void> {
    if (!isPlatformBrowser(this.platformId)) {
      return; // Si on est sur le serveur, on stoppe immédiatement
    }
    const element = this.dashboardElement.nativeElement;

    try {
      // Importation dynamique des deux librairies
      const htmlToImage = await import('html-to-image');
      const jsPDF = (await import('jspdf')).default;

      // 1. Capture en HAUTE DÉFINITION (pixelRatio: 3 évite le flou)
      const dataUrl = await htmlToImage.toPng(element, {
        quality: 1,
        pixelRatio: 3, // Augmenté pour une netteté maximale
        skipFonts: true,
      });

      // 2. Calcul des proportions réelles de votre élément HTML
      const elementWidth = element.offsetWidth;
      const elementHeight = element.offsetHeight;

      // On fixe la largeur du PDF au format A4 standard (210mm)
      const pdfWidth = 210;

      // RÈGLE DE TROIS : On calcule la hauteur proportionnelle en mm pour ne rien couper
      const pdfHeight = (elementHeight * pdfWidth) / elementWidth;

      // 3. Création d'un PDF sur-mesure (on adapte la hauteur de la page au contenu)
      // Au lieu de forcer 'a4', on passe un tableau [largeur, hauteur] personnalisé
      const pdf = new jsPDF({
        orientation: 'p',
        unit: 'mm',
        format: [pdfWidth, pdfHeight], // La page fera exactement la hauteur de votre dashboard !
      });
      // 4. Ajout de l'image qui occupera 100% de la page créée
      pdf.addImage(
        dataUrl,
        'PNG',
        0,
        0,
        pdfWidth,
        pdfHeight,
        undefined,
        'FAST'
      );
      // 5. Sauvegarde
      pdf.save('dashboard-' + new Date().getTime() + '.pdf');
    } catch (error) {
      console.error("Erreur d'export avec html-to-image :", error);
    }
  }
}
