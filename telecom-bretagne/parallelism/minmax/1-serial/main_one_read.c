#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

#include "bench.h"
#include "minmax.h"

int main(int argc, char** argv)
{
	if (argc < 2) {
		fprintf(stderr, "Usage: %s n\n", argv[0]);
		return 1;
	}

	const size_t n = atoll(argv[1]);

	uint32_t (*vec)[n] = malloc( n*sizeof(uint32_t));
    if(!vec) {
        fprintf(stderr, "failed to allocate %lu bytes\n", n*sizeof(uint32_t) );
        return 2;
    }

	srand(0); // Constant values over different launches for "random" numbers
	
	uint32_t min, max;

	BENCH_START(minmax);
	
	compute_minmax_one(n, *vec, &min, &max);
	BENCH_END(minmax, "minmax", n, sizeof(uint32_t), 1, sizeof(uint32_t));

	printf("min = %u / max = %u\n", min, max);
    free(vec);

	return 0;
}
