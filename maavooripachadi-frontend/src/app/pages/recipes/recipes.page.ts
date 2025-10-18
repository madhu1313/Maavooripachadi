import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, combineLatest, of } from 'rxjs';
import { map, shareReplay, startWith } from 'rxjs/operators';

interface Recipe {
  id: string;
  title: string;
  heroImage: string;
  durationMinutes: number;
  servings: number;
  difficulty: 'Easy' | 'Intermediate' | 'Advanced';
  category: string;
  summary: string;
  tags: string[];
  ingredients: string[];
  steps: string[];
  chefTip: string;
  pairings: string[];
  recommendedProducts: Array<{ label: string; link: string }>;
  isFeatured?: boolean;
  isSeasonal?: boolean;
}

interface RecipeFilters {
  query: string;
  category: string;
  difficulty: string;
}

interface RecipesViewModel {
  filters: RecipeFilters;
  recipes: Recipe[];
  featured: Recipe[];
  quickIdeas: Recipe[];
  seasonal: Recipe[];
  hasResults: boolean;
}

const PLACEHOLDER = '/assets/images/placeholder-300x300.png';

const RECIPES: Recipe[] = [
  {
    id: 'gongura-pachi-pulusu',
    title: 'Gongura Pachi Pulusu with Mango Avakai',
    heroImage: '/assets/images/recipes/gongura-pachi-pulusu.jpg',
    durationMinutes: 25,
    servings: 4,
    difficulty: 'Easy',
    category: 'Lunch & Dinner',
    summary: 'A tangy no-cook gongura rasam finished with a spoon of mango avakai for layered spice.',
    tags: ['Tangy', 'Comfort Food', 'Vegetarian'],
    ingredients: [
      '2 medium tomatoes, crushed',
      '1 onion, thinly sliced',
      '1 green chilli, slit',
      '1 tbsp Maavoori Mango Avakai',
      '1 cup water or thin tamarind extract',
      'Fresh coriander, chopped',
      'Salt to taste'
    ],
    steps: [
      'Combine crushed tomatoes, onions, green chilli and salt in a clay pot or glass bowl.',
      'Mash gently with your hand to release juices, then add 1 cup water.',
      'Fold in Maavoori Mango Avakai along with a teaspoon of its oil.',
      'Rest for 10 minutes so the flavours meld. Top with coriander and serve with hot rice.'
    ],
    chefTip: 'For extra zing add a spoon of Maavoori lemon pickle right before serving.',
    pairings: ['Steamed rice with ghee', 'Kandipappu podi and roasted papad'],
    recommendedProducts: [
      { label: 'Mango Avakai', link: '/product/mango-avakai' },
      { label: 'Lemon Ginger Pickle', link: '/product/lemon-ginger' }
    ],
    isFeatured: true,
    isSeasonal: true
  },
  {
    id: 'prawns-pickle-uttapam',
    title: 'Prawns Pickle Mini Uttapam',
    heroImage: '/assets/images/recipes/prawns-uttapam.jpg',
    durationMinutes: 18,
    servings: 3,
    difficulty: 'Intermediate',
    category: 'Breakfast & Tiffin',
    summary: 'Fluffy uttapams topped with Maavoori prawns pickle and charred peppers for a brunch twist.',
    tags: ['Seafood', 'Brunch', 'Crowd Favourite'],
    ingredients: [
      '1 cup idli dosa batter',
      '4 tbsp Maavoori Prawns Pickle',
      '1 small onion, finely chopped',
      'Half red bell pepper, diced',
      'Chopped curry leaves',
      'Gingelly oil for cooking'
    ],
    steps: [
      'Heat a cast iron pan and grease lightly with gingelly oil.',
      'Pour spoonfuls of batter into mini discs, top with onions, peppers and curry leaves.',
      'Drizzle oil around the uttapams and cook till golden on one side. Flip briefly.',
      'Finish with half a spoon of prawns pickle on each uttapam and serve hot.'
    ],
    chefTip: 'For a milder version mix the pickle with hung curd and use as a topping.',
    pairings: ['Coconut chutney', 'Filter coffee'],
    recommendedProducts: [
      { label: 'Prawns Pickle', link: '/product/prawns' },
      { label: 'Instant Dosa Batter Mix', link: '/product/instant-dosa-mix' }
    ],
    isFeatured: true
  },
  {
    id: 'millet-kichdi-mix',
    title: 'Millet Kichdi with Kandi Podi Tempering',
    heroImage: '/assets/images/recipes/millet-kichdi.jpg',
    durationMinutes: 35,
    servings: 4,
    difficulty: 'Easy',
    category: 'Lunch & Dinner',
    summary: 'A hearty foxtail millet kichdi tempered with Maavoori kandi podi infused ghee.',
    tags: ['Millet', 'Wholesome', 'Gluten Free'],
    ingredients: [
      '1 cup foxtail millet',
      'Half cup moong dal',
      '2 cups mixed vegetables',
      '1 tsp Maavoori Kandi Podi',
      '1 tsp turmeric powder',
      '2 tbsp ghee',
      'Salt to taste'
    ],
    steps: [
      'Wash millet and dal, soak for 20 minutes and drain.',
      'Pressure cook with vegetables, turmeric and salt for 3 whistles.',
      'Heat ghee in a pan, add kandi podi and sizzle for 10 seconds.',
      'Pour the spiced ghee over the kichdi, fluff and serve warm.'
    ],
    chefTip: 'Stir through a spoon of Maavoori lemon pickle oil for extra tang.',
    pairings: ['Plain curd', 'Papad and salad'],
    recommendedProducts: [
      { label: 'Kandi Podi', link: '/product/kandi-podi' },
      { label: 'Lemon Pickle', link: '/product/andhra-lemon' }
    ]
  },
  {
    id: 'spice-trail-sandwich',
    title: 'Spice Trail Sandwich with Avakai Butter',
    heroImage: PLACEHOLDER,
    durationMinutes: 12,
    servings: 2,
    difficulty: 'Easy',
    category: 'Snacks & Small Plates',
    summary: 'Toasty sandwiches smeared with spiced avakai butter, layered with roasted veggies.',
    tags: ['Snack', 'Vegetarian', 'Quick Fix'],
    ingredients: [
      '4 slices sourdough or multigrain bread',
      '2 tbsp soft butter',
      '1 tbsp Maavoori Avakai Oil',
      'Roasted bell peppers and onions',
      'Sliced paneer or cheese'
    ],
    steps: [
      'Blend soft butter with avakai oil to make a spicy spread.',
      'Butter bread slices generously on both sides.',
      'Layer roasted vegetables and paneer, grill till crisp and golden.',
      'Serve warm with a side of Maavoori podi sprinkled fries.'
    ],
    chefTip: 'Add sliced mango ginger pickle for an aromatic bite.',
    pairings: ['Masala chai', 'Sweet potato fries'],
    recommendedProducts: [
      { label: 'Mango Avakai', link: '/product/mango-avakai' },
      { label: 'Mango Ginger Pickle', link: '/product/mango-ginger' }
    ],
    isSeasonal: true
  },
  {
    id: 'maavoori-thali',
    title: 'Weekend Maavoori Thali',
    heroImage: '/assets/images/recipes/maavoori-thali.jpg',
    durationMinutes: 60,
    servings: 6,
    difficulty: 'Advanced',
    category: 'Festive & Entertaining',
    summary: 'A curated feast featuring gongura mutton pickle, pesara garelu and signature pachadis.',
    tags: ['Entertaining', 'Family Style'],
    ingredients: [
      '1 jar Maavoori Gongura Mutton Pickle',
      'Pesara garelu batter',
      'Steamed rice, ghee, rasam',
      'Seasonal vegetable curry',
      'Curd rice and papad'
    ],
    steps: [
      'Warm gongura mutton pickle gently in a brass pan and set aside.',
      'Fry pesara garelu till crisp and golden.',
      'Lay out rice, ghee, rasam, vegetable curry and papad on banana leaves.',
      'Serve pickle, garelu and curd rice towards the end for a traditional finish.'
    ],
    chefTip: 'Offer a spoon of Maavoori nalla karam for spice enthusiasts.',
    pairings: ['Banana leaf plating', 'Buttermilk spiced with ginger'],
    recommendedProducts: [
      { label: 'Gongura Mutton Pickle', link: '/product/gongura-mutton' },
      { label: 'Nalla Karam', link: '/product/nalla-karam' }
    ],
    isFeatured: true
  },
  {
    id: 'sweet-avakai-crostini',
    title: 'Sweet Avakai Crostini',
    heroImage: PLACEHOLDER,
    durationMinutes: 15,
    servings: 4,
    difficulty: 'Easy',
    category: 'Snacks & Small Plates',
    summary: 'Crisp crostini topped with whipped hung curd and sweet avakai drizzle.',
    tags: ['Appetiser', 'Party'],
    ingredients: [
      '12 baguette slices',
      '1 cup hung curd',
      '2 tbsp Maavoori Sweet Avakai',
      'Fresh mint and pomegranate'
    ],
    steps: [
      'Toast the baguette slices till crisp.',
      'Whisk hung curd till light and spread on crostini.',
      'Top with chopped sweet avakai and its syrupy oil.',
      'Finish with mint and pomegranate seeds.'
    ],
    chefTip: 'Chill the crostini for 10 minutes before serving for a refreshing bite.',
    pairings: ['Sparkling lemonade', 'Roasted peanuts'],
    recommendedProducts: [
      { label: 'Sweet Avakai', link: '/product/sweet-avakai' }
    ]
  }
];

const RECIPE_CATEGORIES = [
  'All',
  'Breakfast & Tiffin',
  'Lunch & Dinner',
  'Snacks & Small Plates',
  'Festive & Entertaining'
];

const DIFFICULTY_LEVELS = ['All', 'Easy', 'Intermediate', 'Advanced'];

@Component({
  selector: 'app-recipes',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, AsyncPipe, RouterLink, ReactiveFormsModule],
  templateUrl: './recipes.page.html',
  styleUrls: ['./recipes.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RecipesPage {
  private readonly fb = inject(FormBuilder);

  readonly categories = RECIPE_CATEGORIES;
  readonly difficultyLevels = DIFFICULTY_LEVELS;
  readonly pantryTips = [
    {
      id: 'prep',
      title: 'Prep smarter',
      description: 'Keep a jar of Maavoori pickle oil to start quick temperings and marinades.'
    },
    {
      id: 'pair',
      title: 'Pair thoughtfully',
      description: 'Balance spice heavy pickles with cooling curd rice or crispy dosas.'
    },
    {
      id: 'store',
      title: 'Store right',
      description: 'Always use a dry spoon and refrigerate artisan pickles after opening.'
    }
  ];

  readonly filterForm = this.fb.nonNullable.group({
    query: [''],
    category: ['All'],
    difficulty: ['All']
  });

  private readonly recipes$ = of(RECIPES).pipe(shareReplay(1));

  private readonly filters$ = this.filterForm.valueChanges.pipe(
    startWith(this.filterForm.value),
    map((value) => ({
      query: (value.query ?? '').trim().toLowerCase(),
      category: value.category ?? 'All',
      difficulty: value.difficulty ?? 'All'
    } satisfies RecipeFilters))
  );

  private readonly filteredRecipes$ = combineLatest<[Recipe[], RecipeFilters]>([
    this.recipes$,
    this.filters$
  ]).pipe(
    map(([recipes, filters]) => {
      return recipes.filter((recipe) => {
        const matchesQuery =
          !filters.query ||
          recipe.title.toLowerCase().includes(filters.query) ||
          recipe.summary.toLowerCase().includes(filters.query) ||
          recipe.tags.some((tag) => tag.toLowerCase().includes(filters.query));

        const matchesCategory = filters.category === 'All' || recipe.category === filters.category;
        const matchesDifficulty = filters.difficulty === 'All' || recipe.difficulty === filters.difficulty;

        return matchesQuery && matchesCategory && matchesDifficulty;
      });
    }),
    map((recipes) => [...recipes].sort((a, b) => a.durationMinutes - b.durationMinutes))
  );

  private readonly featuredRecipes$ = this.recipes$.pipe(
    map((recipes) => recipes.filter((recipe) => recipe.isFeatured).slice(0, 3))
  );

  private readonly quickIdeas$ = this.recipes$.pipe(
    map((recipes) => recipes.filter((recipe) => recipe.durationMinutes <= 20).slice(0, 3))
  );

  private readonly seasonalRecipes$ = this.recipes$.pipe(
    map((recipes) => recipes.filter((recipe) => recipe.isSeasonal).slice(0, 3))
  );

  readonly vm$: Observable<RecipesViewModel> = combineLatest<[RecipeFilters, Recipe[], Recipe[], Recipe[], Recipe[]]>([
    this.filters$,
    this.filteredRecipes$,
    this.featuredRecipes$,
    this.quickIdeas$,
    this.seasonalRecipes$
  ]).pipe(
    map(([filters, recipes, featured, quickIdeas, seasonal]) => ({
      filters,
      recipes,
      featured,
      quickIdeas,
      seasonal,
      hasResults: recipes.length > 0
    }))
  );

  resetFilters(): void {
    this.filterForm.reset({
      query: '',
      category: 'All',
      difficulty: 'All'
    });
  }

  selectCategory(category: string): void {
    this.filterForm.patchValue({ category });
  }

  durationLabel(durationMinutes: number): string {
    if (durationMinutes < 60) {
      return `${durationMinutes} mins`;
    }
    const hours = Math.floor(durationMinutes / 60);
    const minutes = durationMinutes % 60;
    return minutes ? `${hours} hr ${minutes} mins` : `${hours} hr`;
  }
}
