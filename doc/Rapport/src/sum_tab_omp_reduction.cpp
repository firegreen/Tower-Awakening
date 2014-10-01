// g++ -Wall -Wextra -std=c++11 -fopenmp sum_tab_omp_reduction.cpp -o sum_tab_omp_reduc
#include <iostream>
#include <vector>

int main()
{
	// Create vector
	std::vector<int> tab(10); { int value = 0; for (auto & e : tab) { e = value++; } }
	// Sum of vector tab
	int sum = 0;
	#pragma omp parallel for reduction(+:sum)
	for (std::size_t i = 0; i < tab.size(); ++i) { sum += tab.at(i); }
	// Display
	std::cout << "Sum of vector tab is " << sum << std::endl;
	return 0;
}
