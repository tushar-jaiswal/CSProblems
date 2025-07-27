# Author: Tushar Jaiswal
# Creation Date: 2025-07-27

# Part 1 Convert beats into musical note durations in standard notation. Input: "a 1 0 1 1" Output: "a2 a4 a4" Here, a is the instrument name. The sequence of 0 and 1 represents rhythm. "1 0" means a half note (duration of 2 beats), "1 1" means two quarter notes. The test cases also include eighth notes and sixteenth notes.

# Part 2 Convert beats from multiple instruments into musical note durations, grouping instruments that have the same note duration at the same beat. Input: "a 1 0 1 1", "b 0 0 1 0" Output: "a2 [a,b]4 a4" If multiple instruments have notes of the same length starting at the same beat, group them together like [a,b]4. Test cases include scenarios with three or four instruments.

from collections import defaultdict

def beats_to_durations(beats):
    durations = []
    i = 0
    n = len(beats)
    while i < n:
        if beats[i] == '1':
            duration = 1
            j = i + 1
            while j < n and beats[j] == '0':
                duration += 1
                j += 1
            durations.append((i, duration))
            i = j
        else:
            i += 1
    return durations  # List of (start_beat_index, duration)

def convert_single_instrument(line):
    parts = line.strip().split()
    name, rhythm = parts[0], parts[1:]
    durations = beats_to_durations(rhythm)
    result = []
    for _, d in durations:
        result.append(f"{name}{duration_to_notation(d)}")
    return " ".join(result)

def duration_to_notation(duration):
    # Assuming 1 beat = quarter note = "4"
    mapping = {
        1: "4",
        2: "2",
        3: "d2",
        4: "1"
    }
    return mapping.get(duration, f"*{duration}")

def convert_multiple_instruments(lines):
    note_events = defaultdict(list)  # key: (start_index, duration) → [instruments]

    for line in lines:
        parts = line.strip().split()
        name, rhythm = parts[0], parts[1:]
        durations = beats_to_durations(rhythm)
        for start, dur in durations:
            note_events[(start, dur)].append(name)

    # Sort by start time
    result = []
    for (start, dur) in sorted(note_events):
        instruments = sorted(note_events[(start, dur)])
        group = f"[{','.join(instruments)}]" if len(instruments) > 1 else instruments[0]
        result.append(f"{group}{duration_to_notation(dur)}")

    return " ".join(result)

def main():
    # Part 1
    assert convert_single_instrument("a 1 0 1 1") == "a2 a4 a4"
    assert convert_single_instrument("drum 1 1 0 1") == "drum4 drum2 drum4"

    # Part 2
    result = convert_multiple_instruments([
        "a 1 0 1 1",
        "b 0 0 1 0"
    ])
    
    assert result == "a2 a4 b2 a4"

    result = convert_multiple_instruments([
        "a 1 0 0 1",
        "b 1 0 0 1"
    ])
    
    assert result == "[a,b]d2 [a,b]4"

    result = convert_multiple_instruments([
        "a 1 1 1 1",
        "b 0 0 1 1"
    ])
    assert result == "a4 a4 [a,b]4 [a,b]4"

    print("All tests passed.")

if __name__ == "__main__":
    main()
